package com.example.locationmarker.surface;

import android.location.Location;

public class LocationPoint {
    Location location;
    int number;

    public LocationPoint(Location location, int number) {
        this.location = location;
        this.number = number;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "LocationPoint{" +
                "location=" + location +
                ", number=" + number +
                '}';
    }
}
