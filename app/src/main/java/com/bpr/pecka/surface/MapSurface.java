package com.bpr.pecka.surface;

import static com.bpr.pecka.constants.LocationMarkerConstants.DEFAULT_ZOOM;
import static com.google.maps.android.SphericalUtil.interpolate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.bpr.pecka.R;
import com.bpr.pecka.settings.OptionSettings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class MapSurface {
    protected Context context;
    protected GoogleMap googleMap;
    private Polyline polyline;

    public MapSurface(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
    }


    /**
     * Measures distance between two locations in meters.
     *
     * @param locStart start location
     * @param locEnd   end location
     * @return Distance in Meters
     */
    public static double calculateDistanceBetweenLocations(Location locStart, Location locEnd) {

        return distance(locStart.getLatitude(), locEnd.getLatitude(),
                locStart.getLongitude(), locEnd.getLongitude(), 0, 0);
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @return Distance in Meters
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


    /**
     * Show given surface on the map based on location points on its edges.
     *
     * @param surface surface to show on the map.
     */
    public void showSurfaceOnMap(Surface surface) {
        LatLng surfaceCenter = getSurfaceCenterPoint(surface.convertToLatLngList());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surfaceCenter, DEFAULT_ZOOM));

        double polygonArea = surface.computeArea();
        drawPolygon(polygonArea, surface.convertToLatLngList());
        showLocationMarkerOnMap(surface.getPoints());
    }

    protected void showLocationMarkerOnMap(List<LocationPoint> locationPoints) {
        for (LocationPoint locationPoint : locationPoints) {
            googleMap.addMarker(new MarkerOptions()
                    .position(locationPoint.getLatLng())
                    .title("" + locationPoint.getOrderNumber())
                    .icon(BitmapFromVector(R.drawable.edge_dot))
                    .anchor(0.5f, 0.5f)
            );
        }
    }

    /**
     * Get center of given points. This is needed to recenter the map on selected surface.
     *
     * @param polygonPointsList location points which are vertices of the polygon.
     * @return the center of given points.
     */
    public LatLng getSurfaceCenterPoint(List<LatLng> polygonPointsList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        polygonPointsList.forEach(builder::include);
        return builder.build().getCenter();
    }


    protected BitmapDescriptor BitmapFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    /**
     * Draws polygon on the map based on given location points.
     *
     * @param polygonArea area inside the points.
     * @param points      list of location points which represents vertices of the surface.
     */
    public void drawPolygon(double polygonArea, List<LatLng> points) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(points)
                .fillColor(R.color.black);

        googleMap.addPolygon(polygonOptions);
        LatLng polygonCenter = getSurfaceCenterPoint(points);
        IconGenerator icg = new IconGenerator(context);
        icg.setColor(Color.LTGRAY);
        icg.setTextAppearance(R.style.BlackText);
        Bitmap bm = icg.makeIcon(OptionSettings.getInstance().calculateAreaAccordingToSettingUnit(polygonArea));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(polygonCenter)
                .title(new DecimalFormat("#.00").format(polygonArea))
                .icon(BitmapDescriptorFactory.fromBitmap(bm));

        googleMap.addMarker(markerOptions);
    }


    /**
     * Draws polyline on the map, based on points of the working surface.
     *
     * @param isAddingProcessFinished determines whether adding points to surface process is finished.
     */
    public void drawPolyline(boolean isAddingProcessFinished, Surface surface) {
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.GREEN);
        List<LatLng> points = getLatLngFromLocation(surface);
        polyline = googleMap.addPolyline(polylineOptions.addAll(points));

        writeDistancesOnMap(isAddingProcessFinished, surface);
    }

    private List<LatLng> c(Surface surface) {
        return surface.getPoints().stream()
                .map(LocationPoint::getLatLng)
                .collect(Collectors.toList());
    }

    private void writeDistancesOnMap(boolean isAddingProcessFinished, Surface surface) {
        List<LatLng> points = getLatLngFromLocation(surface);
        int len = points.size();
        LatLng locStart, locEnd;
        for (int i = 0; i < len; i++) {
            locStart = points.get(i);
            if (isAddingProcessFinished && i == len - 1) {
                // if process is finished and the point is the last one, connect first and last point with polyline
                locEnd = points.get(0);
            } else if (i == len - 1) {
                continue;
            } else {
                locEnd = points.get(i + 1);
            }

            double distance = distance(locStart.latitude, locEnd.latitude,
                    locStart.longitude, locEnd.longitude, 0, 0);

            LatLng middle = interpolate(locStart, locEnd, 0.5);

            IconGenerator icg = new IconGenerator(context);
            icg.setColor(Color.GREEN); // transparent background
            icg.setTextAppearance(R.style.BlackText); // black text
            Bitmap bm = icg.makeIcon(OptionSettings.getInstance().calculateDistanceAccordingToSettingUnit(distance));

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(middle.latitude, middle.longitude))
                    .title(new DecimalFormat("#.00").format(distance))
                    .icon(BitmapDescriptorFactory.fromBitmap(bm));

            googleMap.addMarker(markerOptions);
        }
    }

    protected List<LatLng> getLatLngFromLocation(Surface surface) {
        return surface.getPoints().stream()
                .map(LocationPoint::getLatLng)
                .collect(Collectors.toList());
    }

}
