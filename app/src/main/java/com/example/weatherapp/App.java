package com.example.weatherapp;

import android.app.Application;

import androidx.room.Room;

import com.example.weatherapp.data.AppDatabase;
import com.example.weatherapp.data.CityDao;
import com.example.weatherapp.data.WeatherDao;

public class App extends Application {

    private AppDatabase database;
    private CityDao cityDao;
    private WeatherDao weatherDao;

    private static com.example.weatherapp.App instance;

    public static com.example.weatherapp.App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        database = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-db-name")
                .allowMainThreadQueries()
                .build();

        database = Room.databaseBuilder(this.getApplicationContext(),
                AppDatabase.class, "Sample.db")
                .fallbackToDestructiveMigration()
                .build();

        database =  Room.databaseBuilder(this, AppDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();

        cityDao = database.cityDao();
        weatherDao = database.weatherDao();
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public void setCityDao() {
        this.cityDao = cityDao;
    }

    public WeatherDao getWeatherDao() { return weatherDao; }

    public void setWeatherDao(WeatherDao noteDao) {
        this.weatherDao = weatherDao;
    }

}
