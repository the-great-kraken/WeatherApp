package com.example.weatherapp.data;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class CityModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "City")
    public String city;

    protected CityModel(Parcel in) {
        id = in.readInt();
        city = in.readString();
    }

    public static final Creator<CityModel> CREATOR = new Creator<CityModel>() {
        @Override
        public CityModel createFromParcel(Parcel in) {
            return new CityModel(in);
        }

        @Override
        public CityModel[] newArray(int size) {
            return new CityModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityModel cityModel = (CityModel) o;
        return id == cityModel.id &&
                city.equals(cityModel.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city);
    }

    public CityModel() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(city);
    }
}

