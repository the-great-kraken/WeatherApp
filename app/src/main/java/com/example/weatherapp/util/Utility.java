package com.example.weatherapp.util;

import com.example.weatherapp.model.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public static Apis getApis() {
        return Client.getClient().create(Apis.class);
    }

    public interface Apis {
        //By City Name
        @GET("forecast")
        Call<Forecast> getWeatherForecastData(@Query("q") StringBuilder cityName, @Query("APPID") String APIKEY, @Query("units") String TempUnit);
    }

}
