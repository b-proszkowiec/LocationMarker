package com.example.locationmarker.markers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MyMarker{

    // vars
    private LatLng location;
    private String name;
    private static int COUNTER = 0;

    public String getName() {
        return name;
    }

    public MyMarker(LatLng locationLatlng) {
        this.location = locationLatlng;
        this.name = "Marker " + COUNTER;
    }

    public LatLng getLocation() {
        return this.location;
    }
}
