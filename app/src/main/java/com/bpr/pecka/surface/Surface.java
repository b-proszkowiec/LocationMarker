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
    List<LocationPoint> pointsList;
    String name;
    int totalPoints;
    double areaInSquareMeters;

    public Surface(String locationName) {
        this.pointsList = new ArrayList<>();
        this.name = locationName;
        this.totalPoints = 0;
        this.areaInSquareMeters = -1;
    }

    void addPointToSurface(Location location) {
        pointsList.add(new LocationPoint(location, totalPoints++));
    }

    public List<LocationPoint> getPoints() {
        return pointsList;
    }

    public void setName(String locationName) {
        this.name = locationName;
    }

    public List<LatLng> convertToLatLngList() {
        return pointsList.stream()
                .map(LocationPoint::getLatLng)
                .collect(Collectors.toList());
    }

    public double computeArea() {
        areaInSquareMeters = SphericalUtil.computeArea(convertToLatLngList());
        return areaInSquareMeters;
    }

    public String getName() {
        return name;
    }

    public String getArea() {
        return OptionSettings.getInstance().calculateAreaAccordingToSettingUnit(areaInSquareMeters);
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
