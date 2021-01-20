package com.example.locationmarker.surface;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class Surface {
    private static final String LOG_TAG = "Surface";

    List<LocationPoint> locationPoints;
    String locationName;
    int locationPointCounter;
    double areaValue;

    public Surface(String locationName) {
        this.locationPointCounter = 0;
        this.locationName = locationName;
        this.locationPoints = new ArrayList<>();
        this.areaValue = -1;
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

    public List<LatLng> convertToLatLngList() {
        List<LatLng> points = new ArrayList<>();
        for (LocationPoint locationPoint : locationPoints) {
            points.add(new LatLng(locationPoint.getLocation().getLatitude(), locationPoint.getLocation().getLongitude()));
        }
        return points;
    }

    public void computeArea() {
        areaValue = SphericalUtil.computeArea(convertToLatLngList());
    }

    public String getName() {
        return locationName;
    }

    public String getArea() {
        return String.format("%.2f square meters", areaValue);
    }
}
