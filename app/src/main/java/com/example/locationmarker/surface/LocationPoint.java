package com.example.locationmarker.surface;

import android.location.Location;

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

    public float getAccuracy() {
        return accuracy;
    }
}
