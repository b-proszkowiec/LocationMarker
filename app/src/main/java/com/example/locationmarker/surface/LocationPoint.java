package com.example.locationmarker.surface;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class LocationPoint implements Serializable {
    int number;
    double latitude, longitude;
    float accuracy;

    public LocationPoint(Location location, int number) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
        this.number = number;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    public int getOrderNumber() {
        return number;
    }
}
