package com.bpr.pecka.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bpr.pecka.R;
import com.bpr.pecka.event.IMapMarker;
import com.bpr.pecka.settings.OptionSettings;
import com.bpr.pecka.storage.AutoDataStorage;
import com.bpr.pecka.storage.JsonFileStorage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bpr.pecka.surface.Surface.distinctByKey;
import static com.google.maps.android.SphericalUtil.interpolate;
import static java.lang.Integer.parseInt;

import androidx.core.content.ContextCompat;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = SurfaceManager.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;
    private static final SurfaceManager INSTANCE = new SurfaceManager();
    private static final String TEMP_NAME = "Name";

    // vars
    private Context context;
    private Surface lastViewedSurface;
    private Surface workingSurface = new Surface(TEMP_NAME);
    private List<Surface> surfaces = new ArrayList<>();
    private IMapMarker mapMarkerListener;

    private Button surfaceNameButton;
    private Polyline polyline;
    private GoogleMap googleMap;

    private SurfaceManager() {
    }

    /**
     * Sets the value of surface name button.
     *
     */
    public void setSurfaceNameButton(Button surfaceNameButton) {
        this.surfaceNameButton = surfaceNameButton;
    }

    /**
     * Sets the value of the private context field to the specified.
     *
     * @param context specified context value.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Gets a SurfaceManager using the defaults.
     *
     * @return unique instance of SurfaceManager.
     */
    public static SurfaceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Stop adding new points to a working surface and create the polygon on the map.
     * This will be happen by joining the last location point with the first one.
     *
     */
    public void finish() {
        refreshView(true, workingSurface);
    }

    /**
     * Stop adding new points to a working surface and reset the process.
     *
     */
    public void reset() {
        workingSurface.getPoints().clear();
        refreshView(false, workingSurface);
    }

    /**
     * Import surfaces from a json file using specified uri.
     *
     * @param uri represents a Uniform Resource Identifier (URI) reference.
     */
    public void importFromJson(Uri uri) {
        List<Surface> importedSurfaces = JsonFileStorage.importFromFile(context, uri);
        if (importedSurfaces != null) {
            surfaces = mergeSurfacesList(surfaces, importedSurfaces);
            updateSurfaces();
        }
    }

    /**
     * Export surfaces to a json file using specified uri.
     *
     * @param context specified context value.
     * @param uri represents a Uniform Resource Identifier (URI) reference.
     */
    public void exportToJson(Context context, Uri uri) {
        JsonFileStorage instance = new JsonFileStorage();
        instance.exportToFile(context, uri, surfaces);
    }

    public void addMapMarkerListener(IMapMarker mapMarkerFragment) {
        mapMarkerListener = mapMarkerFragment;
    }

    List<Surface> mergeSurfacesList(List<Surface> targetSurfaces, List<Surface> surfacesToMerge) {

        targetSurfaces.addAll(surfacesToMerge);
        return targetSurfaces.stream()
                .filter(distinctByKey(Surface::getName))
                .collect(Collectors.toList());
    }

    /**
     * Add new created surface to surfaces list.
     *
     * @param name name of the surface.
     */
    public void storeNewSurface(String name) {
        workingSurface.setName(name);
        surfaces.add(workingSurface);
        workingSurface = new Surface(TEMP_NAME);

        refreshView(false, workingSurface);
        updateSurfaces();
    }

    /**
     * Update surfaces in a temporary file.
     * This will prevent loss of data after application restart.
     */
    public void updateSurfaces() {
        AutoDataStorage.getInstance().saveData(context, surfaces);
    }

    /**
     * Restore surfaces from a temporary file.
     * This is mostly done after application restart.
     *
     */
    public void restoreSavedSurfaces() {
        List<Surface> restoredSurfaces = (List<Surface>) AutoDataStorage.getInstance().loadData(context);
        if (restoredSurfaces != null) {
            surfaces = mergeSurfacesList(surfaces, restoredSurfaces);
        }
    }

    /**
     * Add new location point to a working surface.
     *
     * @param location new point location
     * @return amount of location points in current surface
     */
    public int addPointToWorkingSurface(Location location) {
        setLastViewedSurface(null);
        if (!workingSurface.getPoints().isEmpty()) {
            LocationPoint lastLocation = workingSurface.getPoints().get(workingSurface.getPoints().size() - 1);
            double distance = calculateDistanceBetweenLocations(lastLocation.getLocation(), location);
            if (distance < 1) {
                Toast.makeText(context, String.format("Minimal distance should be at least %.1f m", 1.0), Toast.LENGTH_SHORT).show();
                return getPointsAmount();
            }
        }
        workingSurface.addPointToSurface(location);
        refreshView(false, workingSurface);
        return getPointsAmount();
    }

    /**
     * Remove selected marker from the map.
     *
     * @param marker marker to remove.
     */
    public void removeMarker(Marker marker) {
        List<LocationPoint> locationPoints = workingSurface.getPoints();
        try {
            final int id = parseInt(marker.getTitle());
            Optional<LocationPoint> markerLocationPoint = locationPoints.stream()
                    .filter(p -> p.getOrderNumber() == id)
                    .findFirst();
            if(markerLocationPoint.isPresent()) {
                workingSurface.getPoints().remove(markerLocationPoint.get());
                refreshView(false, workingSurface);
            }
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
        }
        // add control over button visibility
        mapMarkerListener.onLocationMarkerDelete(locationPoints.size());
    }

    /**
     * Refresh view of the surface on the map.
     *
     * @param isAddingProcessFinished determines whether add points to surface is finished.
     * @param surface current surface.
     */
    public void refreshView(boolean isAddingProcessFinished, Surface surface) {
        showSurfaceOnMap(surface);
        if (isAddingProcessFinished) {
            double polygonArea = surface.computeArea();
            drawPolygon(polygonArea, surface.convertToLatLngList());
            surfaceNameButton.setVisibility(View.VISIBLE);
            surfaceNameButton.setText(surface.getName());
        } else if (surface.getPoints().size() > 1) {
            drawPolyline(false);
        }
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

    public void hideSurfaceButton() {
        surfaceNameButton.setVisibility(View.INVISIBLE);
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

    /**
     * Gets surface which is currently edited.
     *
     * @return working surface.
     */
    public Surface getWorkingSurface() {
        return workingSurface;
    }

    /**
     * Gets list of all surfaces.
     *
     * @return surfaces list.
     */
    public List<Surface> getSurfaces() {
        return surfaces;
    }

    /**
     * Gets a surface which was previously selected to show, otherwise returns null.
     *
     * @return last viewed surface.
     */
    public Surface getLastViewedSurface() {
        return lastViewedSurface;
    }

    /**
     * Sets last viewed surface to selected.
     *
     * @param lastViewedSurface last viewed surface.
     */
    public void setLastViewedSurface(Surface lastViewedSurface) {
        this.lastViewedSurface = lastViewedSurface;
    }

    private int getPointsAmount() {
        return workingSurface.getPoints().size();
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
     * Show given surface on the map based on location points on its edges.
     *
     * @param surface surface to show on the map.
     */
    public void showSurfaceOnMap(Surface surface) {
        googleMap.clear();

        for (LocationPoint locationPoint : surface.getPoints()) {
            googleMap.addMarker(new MarkerOptions()
                    .position(locationPoint.getLatLng())
                    .title("" + locationPoint.getOrderNumber())
                    .icon(BitmapFromVector(R.drawable.edge_dot))
                    .anchor(0.5f, 0.5f)
            );
        }
    }




    private BitmapDescriptor BitmapFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    /**
     * Draws polyline on the map, based on points of the working surface.
     *
     * @param isAddingProcessFinished determines whether adding points to surface process is finished.
     */
    public void drawPolyline(boolean isAddingProcessFinished) {
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.GREEN);
        List<LatLng> points = getLatLngFromLocation();
        polyline = googleMap.addPolyline(polylineOptions.addAll(points));

        writeDistancesOnMap(isAddingProcessFinished);
    }


    private List<LatLng> getLatLngFromLocation() {
        return getWorkingSurface().getPoints().stream()
                .map(LocationPoint::getLatLng)
                .collect(Collectors.toList());
    }




}
