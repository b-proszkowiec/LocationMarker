package com.bpr.pecka.storage;

import android.app.Activity;
import android.content.Context;

import com.bpr.pecka.surface.Surface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static com.bpr.pecka.constants.LocationMarkerConstants.DataStorageConstants.STORAGE_FILE_NAME;

public class DataStorage {
    private static final String LOG_TAG = DataStorage.class.getSimpleName();
    private static final DataStorage INSTANCE = new DataStorage();

    /**
     * Gets a DataStorage using the defaults.
     *
     * @return unique instance of DataStorage.
     */
    public static DataStorage getInstance() {
        return INSTANCE;
    }

    private DataStorage() {
    }

    /**
     * Save surfaces to a temporary file, so it can be restored in case of application restart.
     * Location of the file is set to android default and not need to be known.
     *
     * @param context specified context value.
     * @param surfaces list of surfaces to be stored.
     */
    public void saveData(Context context, List<Surface> surfaces) {
        ObjectOutputStream objectOut = null;

        try {
            FileOutputStream fileOut = context.openFileOutput(STORAGE_FILE_NAME, Activity.MODE_PRIVATE);
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
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Restore surfaces from a temporary file. This procedure is performed after application restart.
     *
     * @param context specified context value.
     * @return Object that represents saved surfaces.
     */
    public Object loadData(Context context) {
        ObjectInputStream objectIn = null;
        Object object = null;
        try {

            FileInputStream fileIn = context.openFileInput(STORAGE_FILE_NAME);
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
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}
