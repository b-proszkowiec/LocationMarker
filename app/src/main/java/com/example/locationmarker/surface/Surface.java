package com.example.locationmarker.surface;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.locationmarker.settings.OptionSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Surface implements Serializable {
    private static final String LOG_TAG = Surface.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;

    // vars
    List<LocationPoint> locationPoints;
    String locationName;
    int locationPointCounter;
    double areaInSquareMeters;

    public Surface(String locationName) {
        this.locationPoints = new ArrayList<>();
        this.locationName = locationName;
        this.locationPointCounter = 0;
        this.areaInSquareMeters = -1;
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
            points.add(locationPoint.getLatLng());
        }
        return points;
    }

    public double computeArea() {
        areaInSquareMeters = SphericalUtil.computeArea(convertToLatLngList());
        return areaInSquareMeters;
    }

    public String getName() {
        return locationName;
    }

    public String getArea() {
        return OptionSettings.getInstance().calculateAreaAccordingToSettingUnit(areaInSquareMeters);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
