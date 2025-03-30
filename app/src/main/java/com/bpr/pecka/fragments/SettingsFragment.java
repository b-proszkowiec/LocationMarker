package com.bpr.pecka.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.bpr.pecka.R;
import com.bpr.pecka.controls.IPrecisionIconVisible;
import com.bpr.pecka.settings.OptionSettings;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();

    private static IPrecisionIconVisible iPrecisionIconVisible;

    /**
     * Register listener for the IPrecisionIconVisible.
     *
     * @param listener listeners to register.
     */
    public static void registerListener(IPrecisionIconVisible listener) {
        iPrecisionIconVisible = listener;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        OptionSettings.getInstance().setShowPrecisionIconStatus(getPrecisionIconVisibleStatus());
        OptionSettings.getInstance().setDistanceUnit(getDistanceUnit());
        OptionSettings.getInstance().setAreaUnit(getAreaUnit());
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
            boolean precisionIconVisibleStatus = getPrecisionIconVisibleStatus();
            Log.d(LOG_TAG, "Precision icon visible changed to: " + precisionIconVisibleStatus);
            iPrecisionIconVisible.onPrecisionIconVisibleChange(precisionIconVisibleStatus);

        } else if (key.equals(getString(R.string.distance_unit_title))) {
            String distanceUnit = getDistanceUnit();
            Log.d(LOG_TAG, "Distance unit changed to: " + distanceUnit);
            OptionSettings.getInstance().setDistanceUnit(distanceUnit);

        } else if (key.equals(getString(R.string.area_unit_title))) {
            String areaUnit = getAreaUnit();
            Log.d(LOG_TAG, "Area unit changed to: " + areaUnit);
            OptionSettings.getInstance().setAreaUnit(areaUnit);

        } else if (key.equals(getString(R.string.theme_title))) {
            Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();

        } else if (key.equals(getString(R.string.measurement_system_title))) {
            Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDistanceUnit() {
        ListPreference distanceUnitPreference = findPreference(getString(R.string.distance_unit_title));
        return distanceUnitPreference.getValue();
    }

    private String getAreaUnit() {
        ListPreference areaUnitPreference = findPreference(getString(R.string.area_unit_title));
        return areaUnitPreference.getValue();
    }

    private boolean getPrecisionIconVisibleStatus() {
        String keyEnableStart = getString(R.string.precision_icon_title);
        SwitchPreferenceCompat enableStart = findPreference(keyEnableStart);
        return enableStart.isChecked();
    }
}
