package com.example.weatherapp.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.App;
import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.data.CityModel;
import com.example.weatherapp.data.WeatherDao;
import com.example.weatherapp.data.WeatherModel;
import com.example.weatherapp.model.DataObject;
import com.example.weatherapp.model.Forecast;
import com.example.weatherapp.util.Constants;
import com.example.weatherapp.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = MainActivity.class.getSimpleName();

    LocationManager locationManager;
    Location location;
    double latitute, longitude;
    Geocoder geocoder;
    StringBuilder addressStringBuilder;

    List<DataObject> weatherList;
    List<List<DataObject>> daysList;

    ImageView imageViewWeatherIcon;
    ImageView searchButton, reloadeButton, saveButton;
    View root;
    TextView tvTodayTemperature, tvTodayDescription, tvTodayWind, tvTodayPressure, tvTodayHumidity, cityName;

    String text = "";
    public static String currentCity = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);

        Constants.TEMP_UNIT = " " + "\u00B0C";
        initMember();
        initUi();

        text = MainActivity.cityName;
        if(!text.equals("")) {
            fetchUpdateOnSearched(text);
            MainActivity.cityName = "";
        }
        else {
            detectLocation();
            if(currentCity.equals("") || currentCity.equals(addressStringBuilder + ""))
                getWeather(addressStringBuilder);
            else fetchUpdateOnSearched(currentCity);
        }

        searchButton = root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByCityName();
            }
        });

        reloadeButton = root.findViewById(R.id.reloade_button);
        reloadeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUi();
                detectLocation();
                getWeather(addressStringBuilder);
            }
        });

        saveButton = root.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityModel newCity = new CityModel();
                newCity.city = cityName.getText().toString();
                if(!isExist(newCity.city)) {
                    App.getInstance().getCityDao().insert(newCity);
                    saveButton.setImageResource(R.drawable.heartfull);
                }
                else {
                    App.getInstance().getCityDao().deleteByCity(newCity.city);
                    saveButton.setImageResource(R.drawable.heart);
                }
            }
        });
        return root;
    }

    // This method gets the device current location to call the weather api by default city
    private void detectLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitute = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getContext(), "Connect to network", Toast.LENGTH_SHORT).show();
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        if (location != null) {
            latitute = location.getLatitude();
            longitude = location.getLongitude();
        }

        try {
            geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitute, longitude, 1);
            addressStringBuilder = new StringBuilder();
            if (addressList.size() > 0) {
                Address locationAddress = addressList.get(0);
                for (int i = 0; i <= locationAddress.getMaxAddressLineIndex(); i++) {
                    locationAddress.getAddressLine(i);
                    addressStringBuilder.append(locationAddress.getLocality());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    // This method shows a dialog to enter the city name to search
    private void searchByCityName() {
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        alert.setTitle(this.getString(R.string.search_title));
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setMaxLines(1);
        input.setSingleLine(true);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                if (!result.isEmpty()) {
                    fetchUpdateOnSearched(result);
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancelled
            }
        });
        alert.show();
    }

    private void fetchUpdateOnSearched(String cityName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cityName);
        getWeather(stringBuilder);
    }

    // This method call the openwheathermap api by city name and onResponseSuccess get current weather
    private void getWeather(StringBuilder addressStringBuilder) {
        Call<Forecast> call = Utility.getApis().getWeatherForecastData(addressStringBuilder, Constants.API_KEY, Constants.UNITS);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.isSuccessful());
                    weatherList = response.body().getDataObjectList();

                    Log.i("DAYSLISTSIZE", daysList.size() + "");

                    switch (weatherList.get(0).getWeather().get(0).getIcon()) {
                        case "01d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky);
                            break;
                        case "01n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky);
                            break;
                        case "02d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud);
                            break;
                        case "02n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud);
                            break;
                        case "03d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                            break;
                        case "03n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                            break;
                        case "04d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds);
                            break;
                        case "04n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds);
                            break;
                        case "09d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain);
                            break;
                        case "09n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain);
                            break;
                        case "10d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_rain);
                            break;
                        case "10n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_rain);
                            break;
                        case "11d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                            break;
                        case "11n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                            break;
                        case "13d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_snow);
                            break;
                        case "13n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_snow);
                            break;
                        case "15d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_mist);
                            break;
                        case "15n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_mist);
                            break;
                    }

                    String city = addressStringBuilder +  "";
                    String tmp = city.toLowerCase();
                    currentCity = tmp.substring(0, 1).toUpperCase() + tmp.substring(1);

                    tvTodayTemperature.setText(Math.round(weatherList.get(0).getMain().getTemp()) + "");
                    tvTodayDescription.setText(weatherList.get(0).getWeather().get(0).getDescription());
                    tvTodayWind.setText(getString(R.string.wind_lable) + " " + weatherList.get(0).getWind().getSpeed() + " " + getString(R.string.wind_unit));
                    tvTodayPressure.setText(getString(R.string.pressure_lable) + " " + weatherList.get(0).getMain().getPressure() + " " + getString(R.string.pressure_unit));
                    tvTodayHumidity.setText(getString(R.string.humidity_lable) + " " + weatherList.get(0).getMain().getHumidity() + " " + getString(R.string.humidity_unit));
                    cityName.setText(currentCity);

                    WeatherModel newWeather = new WeatherModel();
                    newWeather.temp = Math.round(weatherList.get(0).getMain().getTemp());
                    newWeather.city = currentCity;
                    newWeather.describe = weatherList.get(0).getWeather().get(0).getDescription();
                    newWeather.wind = weatherList.get(0).getWind().getSpeed();
                    newWeather.pressure = weatherList.get(0).getMain().getPressure();
                    newWeather.humidity = weatherList.get(0).getMain().getHumidity();

                    if(!App.getInstance().getWeatherDao().isExist())
                        App.getInstance().getWeatherDao().insert(newWeather);
                    else App.getInstance().getWeatherDao().update(newWeather);

                    if(isExist(currentCity)) saveButton.setImageResource(R.drawable.heartfull);
                    else saveButton.setImageResource(R.drawable.heart);
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getContext(), "Something went wrong... Please try later!", Toast.LENGTH_SHORT).show();

                WeatherModel saveModel = App.getInstance().getWeatherDao().latestSave();
                tvTodayTemperature.setText(saveModel.temp + "");
                tvTodayDescription.setText(saveModel.describe);
                tvTodayWind.setText(getString(R.string.wind_lable) + " " + saveModel.wind + " " + getString(R.string.wind_unit));
                tvTodayPressure.setText(getString(R.string.pressure_lable) + " " + saveModel.pressure + " " + getString(R.string.pressure_unit));
                tvTodayHumidity.setText(getString(R.string.humidity_lable) + " " + saveModel.humidity + " " + getString(R.string.humidity_unit));
                cityName.setText(saveModel.city);
            }
        });
    }

    private boolean isExist(String city) {
        List<CityModel> cityDao = App.getInstance().getCityDao().getAll();
        for(CityModel item : cityDao) {
            if(item.city.equals(city)) return true;
        }
        return false;
    }

    private void initUi() {
        imageViewWeatherIcon = root.findViewById(R.id.image_weather);
        tvTodayTemperature = root.findViewById(R.id.temperatureLabel);
        tvTodayDescription = root.findViewById(R.id.description);
        tvTodayWind = root.findViewById(R.id.main_wind_speed);
        tvTodayPressure = root.findViewById(R.id.main_pressure);
        tvTodayHumidity = root.findViewById(R.id.main_humidity);
        cityName = root.findViewById(R.id.nav_header_city);
    }

    private void initMember() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        weatherList = new ArrayList<>();
        daysList = new ArrayList<>();
    }
}