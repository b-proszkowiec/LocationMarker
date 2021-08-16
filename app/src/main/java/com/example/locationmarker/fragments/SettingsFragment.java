package com.example.locationmarker.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.locationmarker.R;
import com.example.locationmarker.controls.IPrecisionIconVisible;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;

    private static IPrecisionIconVisible iPrecisionIconVisible;

    public static void registerListener(IPrecisionIconVisible listener) {
        iPrecisionIconVisible = listener;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.precision_icon_title))) {
            String keyEnableStart = getString(R.string.precision_icon_title);
            SwitchPreferenceCompat enableStart = (SwitchPreferenceCompat)findPreference(keyEnableStart);
            if (enableStart.isChecked()) {
                iPrecisionIconVisible.onPrecisionIconVisibleChange(VISIBLE);
            } else {
                iPrecisionIconVisible.onPrecisionIconVisibleChange(INVISIBLE);
            }
        }

    }
}