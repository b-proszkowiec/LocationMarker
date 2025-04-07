package com.bpr.pecka.surface;

import android.location.Location;

import com.bpr.pecka.settings.OptionSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Surface implements Serializable {
    private static final String LOG_TAG = Surface.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;

    // vars
    String name;
    int totalMeasurements;
    double squareMeters;
    List<LocationPoint> points;

    public Surface(String locationName) {
        this.points = new ArrayList<>();
        this.name = locationName;
        this.totalMeasurements = 0;
        this.squareMeters = -1;
    }

    void addPointToSurface(Location location) {
        points.add(new LocationPoint(location, totalMeasurements++));
    }

    public List<LocationPoint> getPoints() {
        return points;
    }

    public void setName(String locationName) {
        this.name = locationName;
    }

    public List<LatLng> convertToLatLngList() {
        return points.stream()
                .map(LocationPoint::getLatLng)
                .collect(Collectors.toList());
    }

    public double computeArea() {
        squareMeters = SphericalUtil.computeArea(convertToLatLngList());
        return squareMeters;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return OptionSettings.getInstance().calculateAreaAccordingToSettingUnit(squareMeters);
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
