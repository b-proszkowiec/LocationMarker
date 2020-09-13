package com.example.locationmarker.markers;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MarkersContainer {
    private static final String TAG = MarkersContainer.class.getSimpleName();
    // vars
    private static Context context;
    private static MarkersContainer instance;
    private static GoogleMap map;
    private ArrayList<Marker> mMarkersList;

    public static void setMap(GoogleMap map) {
        MarkersContainer.map = map;
    }

    public static void setContext(Context context) {
        MarkersContainer.context = context;
    }

    public static MarkersContainer getInstance() {
        if (instance == null) {
            return new MarkersContainer();
        }
        return instance;
    }

    public MarkersContainer() {
        this.instance = this;
        this.mMarkersList = new ArrayList<Marker>();
    }

    public void addMarker(Location location) {
        if (isEnoughFarDistanceBetweenOtherMarkers(location)) {
            mMarkersList.add(new Marker(location));
        }

        map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Test location " + mMarkersList.size()));
    }

    boolean isEnoughFarDistanceBetweenOtherMarkers(Location location){
        for(Marker marker : mMarkersList) {
            Location markerLocation = marker.getLocation();
            double distance = distance(markerLocation.getLatitude(), location.getLatitude(),
                    markerLocation.getLongitude(), location.getLongitude(),
                    0.0, 0.0);
            //Toast.makeText(context, "Distance is equal to: " + distance, Toast.LENGTH_SHORT).show();

        }
        return true;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2) + Math.pow(el1 - el2, 2);
        return Math.sqrt(distance);
    }

    public double computeArea() {
        if (mMarkersList.size() <= 2) {
            return 0;
        }
        List<LatLng> latLngs = new ArrayList<>();
        for (Marker marker : mMarkersList) {
            latLngs.add(new LatLng(marker.getLocation().getLatitude(), marker.getLocation().getLongitude()));
        }
        double surface = SphericalUtil.computeSignedArea(latLngs);
        Log.d(TAG, "computeArea " + surface);

        map.addPolygon(
                new PolygonOptions().addAll(latLngs).strokeWidth(0).fillColor(Color.RED));

        return surface;
    }
}
