package com.example.locationmarker.surface;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Surface {
    List<LocationPoint> locationPoints;
    int locationPointCounter;
    String locationName;

    public Surface(String locationName) {
        this.locationPointCounter = 0;
        this.locationName = locationName;
        this.locationPoints = new ArrayList<>();
    }

    void addPointToSurface(Location location) {
        locationPoints.add(new LocationPoint(location, locationPointCounter++));
    }

    public List<LocationPoint> getLocationPoints() {
        return locationPoints;
    }

    public void setName(String locationName) {
        this.locationName = locationName;
    }

}
