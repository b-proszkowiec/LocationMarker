package com.example.locationmarker.surface;

import android.location.Location;
import android.util.Log;

import com.example.locationmarker.markers.MarkersContainer;
import com.google.android.gms.common.logging.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = "SurfaceManager";
    private static final SurfaceManager INSTANCE = new SurfaceManager();
    private static final String FILE_NAME = "pecki.txt";
    private static final String TEMP_NAME = "Name";

    // vars
    Surface currentSurface = new Surface(TEMP_NAME);
    List<Surface> surfaces = new ArrayList<>();

    private SurfaceManager() {
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

    public void save(String name) {
        currentSurface.setName(name);
        surfaces.add(currentSurface);
        currentSurface = new Surface(TEMP_NAME);

        refreshView(false);

        try {
            serializeData();
        } catch(IOException e) {
            Log.e(LOG_TAG, "IOException exception occured: " + e.toString());
        }
    }

    public void restoreSavedSurfaces() {
        try {
            deserializeData();
        } catch(IOException e) {
            Log.e(LOG_TAG, "IOException exception occured: " + e.toString());
        }
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
            Location location = locationPoint.getLocation();
            MarkersContainer.getInstance().addMarker(location);
        }
        MarkersContainer.getInstance().drawPolyline(isAddingProcessFinished);

        if (isAddingProcessFinished) {
            currentSurface.computeArea();
        }
    }

    private void serializeData() throws IOException {
        try (FileOutputStream f = new FileOutputStream(FILE_NAME);
             ObjectOutput s = new ObjectOutputStream(f)) {
            s.writeObject(surfaces);
        }
    }

    private void deserializeData() throws IOException {
        try (FileInputStream in = new FileInputStream(FILE_NAME);
             ObjectInputStream s = new ObjectInputStream(in)) {
            surfaces = (List) s.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Surface getCurrentSurface() {
        return currentSurface;
    }

    public List<Surface> getSurfaces() {
        return surfaces;
    }
}
