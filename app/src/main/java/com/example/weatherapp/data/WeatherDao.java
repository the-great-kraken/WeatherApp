package com.example.weatherapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherapp.data.WeatherModel;
import com.example.weatherapp.model.Weather;

import java.util.List;

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM WeatherModel")
    List<WeatherModel> getAll();

    @Query("SELECT * FROM WeatherModel")
    LiveData<List<WeatherModel>> getAllLiveData();

    @Query("SELECT * FROM WeatherModel WHERE id IN (:wdataIds)")
    List<WeatherModel> loadAllByIds(int[] wdataIds);

    @Query("SELECT * FROM WeatherModel WHERE id = :id LIMIT 1")
    WeatherModel findById(int id);

    @Query("SELECT EXISTS(SELECT * FROM WeatherModel)")
    Boolean isExist();

    @Query("SELECT * FROM WeatherModel ORDER BY id DESC LIMIT 1")
    WeatherModel latestSave();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WeatherModel wdata);

    @Update
    void update(WeatherModel wdata);

    @Delete
    void delete(WeatherModel wdata);
}
