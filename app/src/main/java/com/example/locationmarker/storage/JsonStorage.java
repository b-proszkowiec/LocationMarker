package com.example.locationmarker.storage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.locationmarker.ItemFragment;
import com.example.locationmarker.surface.Surface;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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


    public static List<Surface> importFromFile(Context context, Activity activity) {

//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        try {
//            startActivityForResult( activity,
//                    Intent.createChooser(intent, "Select a File to Upload"),
//                    FILE_SELECT_CODE,
//                    null);
//        } catch (android.content.ActivityNotFoundException ex) {
//            // Potentially direct the user to the Market with a Dialog
//            Toast.makeText(context, "Please install a File Manager.",
//                    Toast.LENGTH_SHORT).show();
//        }
//
//        String ret = "";
//        try {
//            InputStream inputStream = context.openFileInput("config.txt");
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append("\n").append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        }
//        catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//
//        Type surfaceALType = new TypeToken<ArrayList<Surface>>(){}.getType();
//        List<Surface> surfaces = new Gson().fromJson(ret, surfaceALType);
//
//        return surfaces;
        return null;
    }
}

