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
        refreshView(true);
    }

    public void reset() {
        currentSurface.getLocationPoints().clear();
        refreshView(false);
    }

    public void storeNewSurface(String name) {
        currentSurface.setName(name);
        surfaces.add(currentSurface);
        currentSurface = new Surface(TEMP_NAME);

        refreshView(false);
        storeCurrentSurfaces();
    }

    public void storeCurrentSurfaces() {
        DataStorage.getInstance().saveData(context, surfaces);
    }

    public void restoreSavedSurfaces() {
        surfaces = (List<Surface>) DataStorage.getInstance().loadData(context);
    }

    public int addPointToCurrentLocation(Location location) {
        currentSurface.addPointToSurface(location);
        refreshView(false);
        return getPointsAmount();
    }

    private int getPointsAmount() {
        return currentSurface.getLocationPoints().size();
    }

    private void refreshView(boolean isAddingProcessFinished) {
        MarkersContainer.getInstance().clearMarkersList();

        for (LocationPoint locationPoint : currentSurface.getLocationPoints()) {
            MarkersContainer.getInstance().addMarker(locationPoint.getLatLng());
        }
        MarkersContainer.getInstance().drawPolyline(isAddingProcessFinished);

        if (isAddingProcessFinished) {
            currentSurface.computeArea();
        }
    }

    public Surface getCurrentSurface() {
        return currentSurface;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
}
