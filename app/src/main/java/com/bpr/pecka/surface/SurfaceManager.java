package com.bpr.pecka.surface;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bpr.pecka.event.IMapMarker;
import com.bpr.pecka.markers.MarkersManager;
import com.bpr.pecka.storage.DataStorage;
import com.bpr.pecka.storage.JsonStorage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bpr.pecka.surface.Surface.distinctByKey;
import static java.lang.Integer.parseInt;

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
        workingSurface.getPointsList().clear();
        refreshView(false, workingSurface);
    }

    /**
     * Import surfaces from a json file using specified uri.
     *
     * @param uri represents a Uniform Resource Identifier (URI) reference.
     */
    public void importFromJson(Uri uri) {
        List<Surface> importedSurfaces = JsonStorage.importFromFile(context, uri);
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
        JsonStorage instance = new JsonStorage();
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
        DataStorage.getInstance().saveData(context, surfaces);
    }

    /**
     * Restore surfaces from a temporary file.
     * This is mostly done after application restart.
     *
     */
    public void restoreSavedSurfaces() {
        List<Surface> restoredSurfaces = (List<Surface>) DataStorage.getInstance().loadData(context);
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
        if (!workingSurface.getPointsList().isEmpty()) {
            LocationPoint lastLocation = workingSurface.getPointsList().get(workingSurface.getPointsList().size() - 1);
            double distance = MarkersManager.calculateDistanceBetweenLocations(lastLocation.getLocation(), location);
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
        List<LocationPoint> locationPoints = workingSurface.getPointsList();
        try {
            final int id = parseInt(marker.getTitle());
            Optional<LocationPoint> markerLocationPoint = locationPoints.stream()
                    .filter(p -> p.getOrderNumber() == id)
                    .findFirst();
            if(markerLocationPoint.isPresent()) {
                workingSurface.getPointsList().remove(markerLocationPoint.get());
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
        MarkersManager.getInstance(context).showSurfaceOnMap(surface);
        if (isAddingProcessFinished) {
            double polygonArea = surface.computeArea();
            MarkersManager.getInstance(context).drawPolygon(polygonArea, surface.convertToLatLngList());
            surfaceNameButton.setVisibility(View.VISIBLE);
            surfaceNameButton.setText(surface.getName());
        } else if (surface.getPointsList().size() > 1) {
            MarkersManager.getInstance(context).drawPolyline(false);
        }
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
        return workingSurface.getPointsList().size();
    }
}
