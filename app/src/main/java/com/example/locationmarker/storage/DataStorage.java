package com.example.locationmarker.storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class DataStorage {
    public static final String SHARED_PREFS = "peckiSharedPrefs";
    public static final String TEXT = "text";

    public void saveData(Application mainContext) {
        SharedPreferences sharedPreferences = mainContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, "");
    }

    public void loadData(Application mainContext) {
        SharedPreferences sharedPreferences = mainContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

    }
}

