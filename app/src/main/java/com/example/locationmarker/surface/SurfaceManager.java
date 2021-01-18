package com.example.locationmarker.surface;

import android.location.Location;
import android.util.Log;

import com.example.locationmarker.markers.MarkersContainer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class SurfaceManager implements Serializable {
    private static final String LOG_TAG = "SurfaceManager";
    private static final SurfaceManager INSTANCE = new SurfaceManager();
    private static final String FILE_NAME = "pecki.txt";

    // vars
    Surface currentSurface = new Surface("temporary");
    List<Surface> surfaces;

    private SurfaceManager() {
        Log.d(LOG_TAG, "");
    }

    public static SurfaceManager getInstance() {
        return INSTANCE;
    }

    public void finish() {
        refreshView(true);
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
            double surface =  MarkersContainer.getInstance().computeArea();
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
}
