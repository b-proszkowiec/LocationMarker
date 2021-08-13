package com.example.locationmarker.surface;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.locationmarker.markers.MarkersManager;
import com.example.locationmarker.storage.DataStorage;
import com.example.locationmarker.storage.JsonStorage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.locationmarker.surface.Surface.distinctByKey;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = SurfaceManager.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;
    private static final SurfaceManager INSTANCE = new SurfaceManager();
    private static final String TEMP_NAME = "Name";
    private Context context;

    // vars
    Surface currentSurface = new Surface(TEMP_NAME);
    List<Surface> surfaces = new ArrayList<>();

    private SurfaceManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static SurfaceManager getInstance() {
        return INSTANCE;
    }

    public void finish() {
        refreshView(true, currentSurface);
    }

    public void reset() {
        currentSurface.getLocationPoints().clear();
        refreshView(false, currentSurface);
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
        currentSurface.setName(name);
        surfaces.add(currentSurface);
        currentSurface = new Surface(TEMP_NAME);

        refreshView(false, currentSurface);
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

    public int addPointToCurrentLocation(Location location) {
        if (currentSurface.getLocationPoints().size() > 0) {
            LocationPoint lastLocation = currentSurface.getLocationPoints().get(currentSurface.getLocationPoints().size() - 1);
            double distance = MarkersManager.calculateDistanceBetweenLocations(lastLocation.getLocation(), location);
            if (distance < 1) {
                Toast.makeText(context, String.format("Minimal distance should be at least %.1f m", 1.0), Toast.LENGTH_SHORT).show();
                return getPointsAmount();
            }
        }
        currentSurface.addPointToSurface(location);
        refreshView(false, currentSurface);
        return getPointsAmount();
    }

    private int getPointsAmount() {
        return currentSurface.getLocationPoints().size();
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
        } else {
            MarkersManager.getInstance().drawPolyline(false);
        }
    }

    public Surface getCurrentSurface() {
        return currentSurface;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
}
