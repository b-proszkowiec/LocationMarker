package com.example.locationmarker.settings;

public class OptionSettings {
    private static final String LOG_TAG = OptionSettings.class.getSimpleName();
    private static final OptionSettings INSTANCE = new OptionSettings();

    // vars
    private static boolean showPrecisionIconStatus;
    private static String distanceUnit;

    public static OptionSettings getInstance() {
        return INSTANCE;
    }

    private OptionSettings() {
    }


    public boolean getShowPrecisionIconStatus() {
        return showPrecisionIconStatus;
    }

    public void setShowPrecisionIconStatus(boolean showPrecisionIconStatus) {
        OptionSettings.showPrecisionIconStatus = showPrecisionIconStatus;
    }

    public String calculateDistanceAccordingToSettingUnit(double distance) {
        if (distanceUnit.equals("m")) {
            return String.format("%.2f ", distance) + "m";
        } else if (distanceUnit.equals("km")) {
            return String.format("%.3f ", distance / 1000) + "km";
        } else {
            return "";
        }
    }

    public void setDistanceUnit(String distanceUnit) {
        OptionSettings.distanceUnit = distanceUnit;
    }
}
