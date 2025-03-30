package com.bpr.pecka.settings;

public class OptionSettings {
    private static final String LOG_TAG = OptionSettings.class.getSimpleName();
    private static final OptionSettings INSTANCE = new OptionSettings();

    // vars
    private static boolean showPrecisionIconStatus;
    private static String distanceUnit;
    private static String areaUnit;

    /**
     * Gets a OptionSettings using the defaults.
     *
     * @return unique instance of OptionSettings.
     */
    public static OptionSettings getInstance() {
        return INSTANCE;
    }

    private OptionSettings() {
    }

    /**
     * Gets whether precision icon status button is on/off.
     *
     * @return Status of enabling precision icon button.
     */
    public boolean getShowPrecisionIconStatus() {
        return showPrecisionIconStatus;
    }

    /**
     * Sets whether precision icon status button is on/off.
     *
     * @param showPrecisionIconStatus on/off status of the button.
     */
    public void setShowPrecisionIconStatus(boolean showPrecisionIconStatus) {
        OptionSettings.showPrecisionIconStatus = showPrecisionIconStatus;
    }

    /**
     * Converts distance unit from the meters to the one in preferences.
     *
     * @param distance distance in meters.
     * @return Distance by the unit set in preferences.
     */
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

    /**
     * Converts area unit from the square meters to the one in preferences.
     *
     * @param areaInSquareMeters area in square meters.
     * @return Area by the unit set in preferences.
     */
    public String calculateAreaAccordingToSettingUnit(double areaInSquareMeters) {
        String formattedArea;

        switch (areaUnit) {
            case "m\u00B2":        // square meters
                formattedArea = String.format("%.2f m\u00B2", areaInSquareMeters);
                break;
            case "ar":
                formattedArea = String.format("%.2f ar", areaInSquareMeters / 100);
                break;
            case "ha":
                formattedArea = String.format("%.2f ha", areaInSquareMeters / (10*1000));
                break;
            case "km\u00B2":
                formattedArea = String.format("%.2f km\u00B2", areaInSquareMeters / (1000*1000));
                break;
            default:
                formattedArea = "n/a";
                break;
        }
        return formattedArea;
    }

    /**
     * Sets distance unit.
     *
     * @param distanceUnit distance unit string.
     */
    public void setDistanceUnit(String distanceUnit) {
        OptionSettings.distanceUnit = distanceUnit;
    }

    /**
     * Sets area unit.
     *
     * @param areaUnit area unit string.
     */
    public static void setAreaUnit(String areaUnit) {
        OptionSettings.areaUnit = areaUnit;
    }
}
