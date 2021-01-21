package com.example.locationmarker.surface;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.locationmarker.markers.MarkersContainer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    public void save(Context context, String name) {
        currentSurface.setName(name);
        surfaces.add(currentSurface);
        currentSurface = new Surface(TEMP_NAME);

        refreshView(false);

        try {
            serializeData(context);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException exception occured: " + e.toString());
        }
    }

    public void restoreSavedSurfaces(Context context) {
        try {
            surfaces = (List<Surface>) deserializeData(context);
        } catch (IOException e) {
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

    //
    private void serializeData(Context context) throws IOException {
        ObjectOutputStream objectOut = null;

        try {
            FileOutputStream fileOut = context.openFileOutput(FILE_NAME, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(surfaces);
            fileOut.getFD().sync();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
    }

    private Object deserializeData(Context context) throws IOException {
        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = context.openFileInput(FILE_NAME);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
        return object;
    }
}
