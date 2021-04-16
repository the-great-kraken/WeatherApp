package com.example.weatherapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.weatherapp.model.Weather;

import java.util.Objects;

@Entity
public class WeatherModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "City")
    public String city;

    @ColumnInfo(name = "Temperature")
    public int temp;

    @ColumnInfo(name = "Describe")
    public String describe;

    @ColumnInfo(name = "Wind")
    public float wind;

    @ColumnInfo(name = "Humidity")
    public int humidity;

    @ColumnInfo(name = "Pressure")
    public float pressure;

    protected WeatherModel(Parcel in) {
        id = in.readInt();
        city = in.readString();
        temp = in.readInt();
        describe = in.readString();
        wind = in.readFloat();
        humidity = in.readInt();
        pressure = in.readFloat();
    }

    public static final Creator<WeatherModel> CREATOR = new Creator<WeatherModel>() {
        @Override
        public WeatherModel createFromParcel(Parcel in) {
            return new WeatherModel(in);
        }

        @Override
        public WeatherModel[] newArray(int size) {
            return new WeatherModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherModel weatherModel = (WeatherModel) o;
        return id == weatherModel.id &&
                city.equals(weatherModel.city) &&
                temp == weatherModel.temp &&
                describe.equals(weatherModel.describe) &&
                wind == weatherModel.wind &&
                humidity == weatherModel.humidity &&
                pressure == weatherModel.pressure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, temp, describe, wind, humidity, pressure);
    }

    public WeatherModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(city);
        dest.writeInt(temp);
        dest.writeString(describe);
        dest.writeFloat(wind);
        dest.writeInt(humidity);
        dest.writeFloat(pressure);
    }
}
