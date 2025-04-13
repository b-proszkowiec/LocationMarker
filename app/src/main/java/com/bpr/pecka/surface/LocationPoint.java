package com.bpr.pecka.surface;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class LocationPoint implements Serializable {
    int number;
    boolean reference;
    double latitude;
    double longitude;
    double altitude;
    float accuracy;

    public LocationPoint(Location location, int number) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
        this.altitude = location.getAltitude();
        this.reference = false;
        this.number = number;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public double getAltitude() {
        return altitude;
    }

    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);
        return location;
    }

    public int getOrderNumber() {
        return number;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }
}
