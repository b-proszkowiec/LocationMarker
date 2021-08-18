package com.example.locationmarker.surface;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.locationmarker.markers.MarkersManager;
import com.example.locationmarker.storage.DataStorage;
import com.example.locationmarker.storage.JsonStorage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.locationmarker.fragments.MapFragment.updateBottomLayer;
import static com.example.locationmarker.surface.Surface.distinctByKey;
import static java.lang.Integer.parseInt;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = SurfaceManager.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;
    private static final SurfaceManager INSTANCE = new SurfaceManager();
    private static final String TEMP_NAME = "Name";
    private Context context;

    // vars
    private Surface lastViewedSurface;
    private Surface workingSurface = new Surface(TEMP_NAME);
    private List<Surface> surfaces = new ArrayList<>();

    private SurfaceManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static SurfaceManager getInstance() {
        return INSTANCE;
    }

    public void finish() {
        refreshView(true, workingSurface);
    }

    public void reset() {
        workingSurface.getLocationPoints().clear();
        refreshView(false, workingSurface);
    }

    public void exportToJson(Context context, Uri uri) {
        JsonStorage instance = new JsonStorage();
        instance.exportToFile(context, uri, surfaces);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    List<Surface> mergeSurfacesList(List<Surface> targetSurfaces, List<Surface> surfacesToMerge) {

        targetSurfaces.addAll(surfacesToMerge);
        return targetSurfaces.stream()
                .filter(distinctByKey(p -> p.getName()))
                .collect(Collectors.toList());
    }

    public void importFromJson(Uri uri) {
        List<Surface> importedSurfaces = JsonStorage.importFromFile(context, uri);
        if (importedSurfaces != null) {
            surfaces = mergeSurfacesList(surfaces, importedSurfaces);
            storeCurrentSurfaces();
        }
    }

    public void storeNewSurface(String name) {
        workingSurface.setName(name);
        surfaces.add(workingSurface);
        workingSurface = new Surface(TEMP_NAME);

        refreshView(false, workingSurface);
        storeCurrentSurfaces();
    }

    public void storeCurrentSurfaces() {
        DataStorage.getInstance().saveData(context, surfaces);
    }

    public void restoreSavedSurfaces() {
        List<Surface> restoredSurfaces = (List<Surface>) DataStorage.getInstance().loadData(context);
        if (restoredSurfaces != null) {
            surfaces = mergeSurfacesList(surfaces, restoredSurfaces);
        }
    }

    public int addPointToWorkingSurface(Location location) {
        setLastViewedSurface(null);
        if (workingSurface.getLocationPoints().size() > 0) {
            LocationPoint lastLocation = workingSurface.getLocationPoints().get(workingSurface.getLocationPoints().size() - 1);
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

    public void removeMarker(Marker marker) {
        try {
            final int id = parseInt(marker.getTitle());
            Optional<LocationPoint> markerLocationPoint = workingSurface.getLocationPoints().stream()
                    .filter(p -> p.getOrderNumber() == id)
                    .findFirst();
            if(markerLocationPoint.isPresent()) {
                workingSurface.getLocationPoints().remove(markerLocationPoint.get());
                refreshView(false, workingSurface);
            }
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
        }
        // add control over button visibility
        updateBottomLayer(workingSurface.getLocationPoints().size());
    }

    private int getPointsAmount() {
        return workingSurface.getLocationPoints().size();
    }

    public LatLng getSurfaceCenterPoint(List<LatLng> polygonPointsList) {
        LatLng centerLatLng;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    public void refreshView(boolean isAddingProcessFinished, Surface surface) {
        MarkersManager.getInstance().showSurfaceOnMap(surface);
        if (isAddingProcessFinished) {
            double polygonArea = surface.computeArea();
            MarkersManager.getInstance().drawPolygon(polygonArea, surface.convertToLatLngList());
        } else if (surface.getLocationPoints().size() > 1) {
            MarkersManager.getInstance().drawPolyline(false);
        }
    }

    public Surface getWorkingSurface() {
        return workingSurface;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }

    public Surface getLastViewedSurface() {
        return lastViewedSurface;
    }

    public void setLastViewedSurface(Surface lastViewedSurface) {
        this.lastViewedSurface = lastViewedSurface;
    }
}
