package com.example.locationmarker.storage;

import android.app.Activity;
import android.content.Context;

import com.example.locationmarker.surface.Surface;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class DataStorage {
    private static final String LOG_TAG = DataStorage.class.getSimpleName();
    private static final String FILE_NAME = "pecki.txt";
    private static final DataStorage INSTANCE = new DataStorage();

    public static DataStorage getInstance() {
        return INSTANCE;
    }

    private DataStorage() {
    }

    public void saveData(Context context, List<Surface> surfaces) {
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
                    e.printStackTrace();
                }
            }
        }
    }

    public Object loadData(Context context) {
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
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
}
