package com.example.locationmarker.markers;

import android.location.Location;

public class Marker {

    // vars
    private Location location;

    public Marker(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }
}
