package com.example.weatherapp.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {WeatherModel.class, CityModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao cityDao();
    public abstract WeatherDao weatherDao();
}


