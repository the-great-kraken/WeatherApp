package com.example.weatherapp.places;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.App;
import com.example.weatherapp.data.CityModel;

import java.util.List;

public class PlacelistViewModel extends ViewModel {

    private LiveData<List<CityModel>> noteLiveData = App.getInstance().getCityDao().getAllLiveData();

    public LiveData<List<CityModel>> getNoteLiveData() {
        return noteLiveData;
    }
}