package com.example.locationmarker.storage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.locationmarker.ItemFragment;
import com.example.locationmarker.surface.Surface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonStorage extends Activity {

    private static final String LOG_TAG = ItemFragment.class.getSimpleName();

    public JsonStorage() {
    }

    public void exportToFile(Context context, Uri uri, List<Surface> surfaces) {

        OutputStream outputStream = null;
        String jsonOfSurfacesArray = new Gson().toJson(surfaces);

        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            byte[] strToBytes = jsonOfSurfacesArray.getBytes();
            outputStream.write(strToBytes);

        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "-----> File not found", e);
        } catch (IOException e) {
            Log.d(LOG_TAG, "-----> Error reading file", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.d(LOG_TAG, "-----> Error reading file", e);
                }
            }
        }
    }


    public static List<Surface> importFromFile(Context context, Uri uri) {
        List<Surface> surfaces = null;
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);

            String fileContent = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Type surfaceALType = new TypeToken<ArrayList<Surface>>() {
            }.getType();
            surfaces = new Gson().fromJson(fileContent, surfaceALType);

        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "-----> File not found", e);
        } catch (IOException e) {
            Log.d(LOG_TAG, "-----> Error reading file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(LOG_TAG, "-----> Error reading file", e);
                }
            }
        }
        return surfaces;
    }
}
