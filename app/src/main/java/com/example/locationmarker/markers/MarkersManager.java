package com.example.locationmarker.markers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.example.locationmarker.DetailsActivity;
import com.example.locationmarker.R;
import com.example.locationmarker.settings.OptionSettings;
import com.example.locationmarker.surface.LocationPoint;
import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.google.maps.android.SphericalUtil.interpolate;
import static java.lang.Integer.parseInt;

public class MarkersManager implements Comparator<LatLng> {
    private static final String LOG_TAG = MarkersManager.class.getSimpleName();
    // vars
    private static Context context;
    private static MarkersManager instance;
    private static GoogleMap googleMap;
    private static View transparentView;

    // vars
    private Polyline polyline;

    public static void setContext(Context context) {
        MarkersManager.context = context;
    }

    public static MarkersManager getInstance() {
        if (instance == null) {
            return new MarkersManager();
        }
        return instance;
    }

    private MarkersManager() {
        RelativeLayout mMapViewRoot = ((Activity) context).findViewById(R.id.marker_layout);
        transparentView = View.inflate(context, R.layout.transparent_layout, mMapViewRoot);
        instance = this;
    }

    public static void setGoogleMap(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setOnMarkerClickListener(marker -> {
                    Surface lastViewedSurface = SurfaceManager.getInstance().getLastViewedSurface();
                    if (lastViewedSurface != null) {
                        try {
                            int id = parseInt(marker.getTitle());
                            Optional<LocationPoint> locationPoint = lastViewedSurface.getLocationPoints().stream()
                                    .filter(p -> p.getOrderNumber() == id)
                                    .findFirst();

                            if (locationPoint.isPresent()) {
                                showDetailsLayout(locationPoint.get());
                                return true;
                            }
                            Log.e(LOG_TAG, "Unable to recognized LocationPoint object of selected marker!");
                        } catch (NumberFormatException e) {
                            Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
                        }
                        return false;
                    } else {
                        Projection projection = googleMap.getProjection();
                        LatLng markerLocation = marker.getPosition();
                        Point screenPosition = projection.toScreenLocation(markerLocation);
                        transparentView.setTranslationX(screenPosition.x);
                        transparentView.setTranslationY(screenPosition.y);

                        PopupMenu popupMenu = new PopupMenu(context, transparentView);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getTitle().equals(context.getString(R.string.marker_delete_popup))) {
                                    SurfaceManager.getInstance().removeMarker(marker);
                                }
                                return false;
                            }
                        });

                        popupMenu.inflate(R.menu.marker_popup_menu);
                        popupMenu.show();
                        return true;
                    }
                }
        );
    }

    public void showSurfaceOnMap(Surface surface) {
        googleMap.clear();

        for (LocationPoint locationPoint : surface.getLocationPoints()) {
            googleMap.addMarker(new MarkerOptions()
                    .position(locationPoint.getLatLng())
                    .title("" + locationPoint.getOrderNumber()));
        }
    }

    public static double calculateDistanceBetweenLocations(Location locStart, Location locEnd) {
        double distance = distance(locStart.getLatitude(), locEnd.getLatitude(),
                locStart.getLongitude(), locEnd.getLongitude(), 0, 0);

        return distance;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
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


    public int compare(LatLng o1, LatLng o2) {
        return (int) (o2.latitude - o1.latitude) * 10 * 1000;
    }

    public void drawPolyline(boolean isAddingProcessFinished) {
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.GREEN);
        List<LatLng> points = getLatLngFromLocation();
        polyline = googleMap.addPolyline(polylineOptions.addAll(points));

        writeDistancesOnMap(isAddingProcessFinished);
    }

    public void drawPolygon(double polygonArea, List<LatLng> points) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(points)
                .fillColor(R.color.black);

        googleMap.addPolygon(polygonOptions);
        LatLng polygonCenter = SurfaceManager.getInstance().getSurfaceCenterPoint(points);


        IconGenerator icg = new IconGenerator(context);
        icg.setColor(Color.GREEN); // transparent background
        icg.setTextAppearance(R.style.BlackText); // black text
        Bitmap bm = icg.makeIcon(OptionSettings.getInstance().calculateAreaAccordingToSettingUnit(polygonArea));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(polygonCenter)
                .title(new DecimalFormat("#.00").format(polygonArea))
                .icon(BitmapDescriptorFactory.fromBitmap(bm));

        googleMap.addMarker(markerOptions);
    }

    private List<LatLng> getLatLngFromLocation() {
        List<LatLng> points = new ArrayList<>();
        for (LocationPoint locationPoint : SurfaceManager.getInstance().getWorkingSurface().getLocationPoints()) {
            points.add(locationPoint.getLatLng());
        }
        return points;
    }

    private static void showDetailsLayout(LocationPoint locationPoint) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("LocationPoint", locationPoint);
        context.startActivity(intent);
    }

    private void writeDistancesOnMap(boolean isAddingProcessFinished) {
        List<LatLng> points = getLatLngFromLocation();
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
}
