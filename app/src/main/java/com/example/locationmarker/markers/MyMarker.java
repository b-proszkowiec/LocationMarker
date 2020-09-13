package com.example.locationmarker.markers;

import android.location.Location;

public class MyMarker{

    // vars
    private Location location;
    private String name;
    private static int COUNTER = 0;

    public String getName() {
        return name;
    }

    public MyMarker(Location location) {
        this.location = location;
        this.name = "Marker " + COUNTER;
    }

    public Location getLocation() {
        return this.location;
    }
}
