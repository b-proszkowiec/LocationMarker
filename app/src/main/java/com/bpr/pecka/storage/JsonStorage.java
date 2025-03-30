package com.bpr.pecka.storage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bpr.pecka.surface.Surface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
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

    private static final String LOG_TAG = JsonStorage.class.getSimpleName();

    /**
     * Export surfaces to a selected file on the disk as a JSON.
     *
     * @param context  Specified context value.
     * @param uri      Represents a Uniform Resource Identifier (URI) reference.
     * @param surfaces List of surfaces to be stored.
     */
    public void exportToFile(Context context, Uri uri, List<Surface> surfaces) {
        String jsonOfSurfacesArray = new Gson().toJson(surfaces);

        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) {
                Log.e(LOG_TAG, "Failed to open output stream for URI: " + uri);
                return;
            }
            outputStream.write(jsonOfSurfacesArray.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error exporting data to file: " + uri, e);
        }
    }

    /**
     * Import surfaces from a selected file.
     *
     * @param context Specified context value.
     * @param uri     Represents a Uniform Resource Identifier (URI) reference.
     * @return List of surfaces stored in a file.
     */
    public static List<Surface> importFromFile(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                Log.e(LOG_TAG, "Failed to open input stream for URI: " + uri);
                return new ArrayList<>();
            }

            String fileContent = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Type surfaceListType = new TypeToken<ArrayList<Surface>>() {}.getType();
            return new Gson().fromJson(fileContent, surfaceListType);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error importing data from file: " + uri, e);
        }
        return new ArrayList<>();
    }
}
