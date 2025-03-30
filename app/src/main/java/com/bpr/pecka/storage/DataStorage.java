package com.bpr.pecka.storage;

import static com.bpr.pecka.constants.LocationMarkerConstants.DataStorageConstants.STORAGE_FILE_NAME;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bpr.pecka.surface.Surface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataStorage {
    private static final String LOG_TAG = DataStorage.class.getSimpleName();
    private static final DataStorage INSTANCE = new DataStorage();

    private DataStorage() {
    }

    /**
     * Gets a DataStorage using the defaults.
     *
     * @return unique instance of DataStorage.
     */
    public static DataStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Save surfaces to a temporary file, so it can be restored in case of application restart.
     * Location of the file is set to android default and not need to be known.
     *
     * @param context  specified context value.
     * @param surfaces list of surfaces to be stored.
     */
    public void saveData(Context context, List<Surface> surfaces) {
        try (FileOutputStream fileOut = context.openFileOutput(STORAGE_FILE_NAME, Activity.MODE_PRIVATE);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(surfaces);
            objectOut.flush();
            fileOut.getFD().sync();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error saving data to file: " + STORAGE_FILE_NAME, e);
        }
    }


    /**
     * Restore surfaces from a temporary file. This procedure is performed after application restart.
     *
     * @param context specified context value.
     * @return Object that represents saved surfaces.
     */
    public Object loadData(Context context) {
        Object object = null;
        try (FileInputStream fileIn = context.openFileInput(STORAGE_FILE_NAME);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            object = objectIn.readObject();

        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + STORAGE_FILE_NAME, e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while reading the file", e);
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG, "Class not found during deserialization", e);
        }

        return object;
    }
}
