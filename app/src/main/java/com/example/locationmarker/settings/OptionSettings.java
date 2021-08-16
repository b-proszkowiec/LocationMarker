package com.example.locationmarker.settings;

public class OptionSettings {
    private static final String LOG_TAG = OptionSettings.class.getSimpleName();
    private static final OptionSettings INSTANCE = new OptionSettings();

    // vars
    private static boolean showPrecisionIconStatus;
    private static String distanceUnit;
    private static String areaUnit;

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
        String formattedDistance;

        switch (distanceUnit) {
            case "m":
                formattedDistance = String.format("%.2f ", distance) + "m";
                break;
            case "km":
                formattedDistance = String.format("%.3f ", distance / 1000) + "km";
                break;
            default:
                formattedDistance = "";
        }
        return formattedDistance;
    }

    public void setDistanceUnit(String distanceUnit) {
        OptionSettings.distanceUnit = distanceUnit;
    }

    public String calculateAreaAccordingToSettingUnit(double areaInSquareMeters) {
        String formattedArea;

        switch (areaUnit) {
            case "m\u00B2":        // square meters
                formattedArea = String.format("%.2f m\u00B2", areaInSquareMeters);
                break;
            case "ar":
                formattedArea = String.format("%.2f ares", areaInSquareMeters / 100);
                break;
            case "ha":
                formattedArea = String.format("%.2f hectares", areaInSquareMeters / 10*1000);
                break;
            case "km\u00B2":
                formattedArea = String.format("%.2f km\u00B2", areaInSquareMeters / 1000*1000);
                break;
            default:
                formattedArea = "n/a";
                break;
        }
        return formattedArea;
    }

    public static void setAreaUnit(String areaUnit) {
        OptionSettings.areaUnit = areaUnit;
    }
}
