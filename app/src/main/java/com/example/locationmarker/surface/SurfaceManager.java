package com.example.locationmarker.surface;

import android.content.Context;
import android.location.Location;

import com.example.locationmarker.markers.MarkersContainer;
import com.example.locationmarker.storage.DataStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = SurfaceManager.class.getSimpleName();
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
            surfaces.addAll(restoredSurfaces);
        }
    }

    public int addPointToCurrentLocation(Location location) {
        currentSurface.addPointToSurface(location);
        refreshView(false, currentSurface);
        return getPointsAmount();
    }

    private int getPointsAmount() {
        return currentSurface.getLocationPoints().size();
    }

    public void refreshView(boolean isAddingProcessFinished, Surface surface) {
        MarkersContainer.getInstance().clearMarkersList();

        for (LocationPoint locationPoint : surface.getLocationPoints()) {
            MarkersContainer.getInstance().addMarker(locationPoint.getLatLng());
        }

        if (isAddingProcessFinished) {
            double polygonArea = surface.computeArea();
            MarkersContainer.getInstance().drawPolygon(polygonArea, surface.convertToLatLngList());
        } else {
            MarkersContainer.getInstance().drawPolyline(isAddingProcessFinished);
        }
    }

    public Surface getCurrentSurface() {
        return currentSurface;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
}
