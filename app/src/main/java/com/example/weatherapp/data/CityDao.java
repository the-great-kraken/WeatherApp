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
public interface CityDao {

    @Query("SELECT * FROM CityModel")
    List<CityModel> getAll();

    @Query("SELECT * FROM CityModel")
    LiveData<List<CityModel>> getAllLiveData();

    @Query("SELECT * FROM CityModel WHERE id IN (:cityIds)")
    List<CityModel> loadAllByIds(int[] cityIds);

    @Query("SELECT * FROM CityModel WHERE id = :id LIMIT 1")
    CityModel findById(int id);

    @Query("SELECT EXISTS(SELECT * FROM CityModel WHERE city = :city)")
    Boolean isRowIsExist(String city);

    @Query("DELETE FROM CityModel WHERE city = :city")
    void deleteByCity(String city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CityModel cdata);

    @Update
    void update(CityModel cdata);

    @Delete
    void delete(CityModel cdata);

}
