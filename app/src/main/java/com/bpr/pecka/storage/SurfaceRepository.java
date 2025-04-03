package com.bpr.pecka.storage;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.bpr.pecka.surface.Surface;

import java.util.ArrayList;
import java.util.List;

public class SurfaceRepository {

    private static List<Surface> surfaces;
    private static Context context;


    @SuppressWarnings("unchecked")
    public static void restoreSavedSurfaces(@NonNull Context ctx) {
        context = ctx;
        Object data = AutoDataStorage.getInstance().loadData(context);

        if (data instanceof List<?>) {
            try {
                surfaces = (List<Surface>) data;
            } catch (ClassCastException e) {
                surfaces = new ArrayList<>();
            }
        } else {
            surfaces = new ArrayList<>();
        }
    }

    public static void updateInAutoStorage() {
        AutoDataStorage.getInstance().saveData(context, surfaces);
    }

    public static boolean addSurface(Surface surface) {
        boolean alreadyExist = surfaces.stream().anyMatch(p -> p.getName().equals(surface.getName()));
        if (!alreadyExist) {
            surfaces.add(surface);
            AutoDataStorage.getInstance().saveData(context, surfaces);
        }
        return false;
    }


    public static boolean removeSurface(Surface surface) {
        boolean status =  surfaces.removeIf(p -> p.getName().equals(surface.getName()));
        updateInAutoStorage();
        return status;
    }


    public static List<Surface> getSurfaces() {
        return surfaces;
    }

    public static List<Surface> importFromJsonFile(Context context, Uri uri) {
        surfaces = JsonFileStorage.importFromFile(context, uri);
        updateInAutoStorage();
        return  surfaces;
    }

    public static void exportToJsonFile(Context context, Uri uri) {
        JsonFileStorage.exportToFile(context, uri, surfaces);
    }

}
