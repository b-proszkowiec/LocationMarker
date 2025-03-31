package com.bpr.pecka.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OptionSettings {
    private static final String LOG_TAG = OptionSettings.class.getSimpleName();
    private static final OptionSettings instance = new OptionSettings();

    private boolean showPrecisionIconStatus = false;
    private String distanceUnit = "m";
    private String areaUnit = "m²";

    private OptionSettings() {
    }

    /**
     * Gets a unique instance of OptionSettings.
     *
     * @return Singleton instance of OptionSettings.
     */
    @NonNull
    public static OptionSettings getInstance() {
        return instance;
    }

    /**
     * Gets whether precision icon status button is on/off.
     *
     * @return Status of enabling precision icon button.
     */
    public boolean isShowPrecisionIconStatus() {
        return showPrecisionIconStatus;
    }

    /**
     * Sets whether precision icon status button is on/off.
     *
     * @param showPrecisionIconStatus on/off status of the button.
     */
    public void setShowPrecisionIconStatus(boolean showPrecisionIconStatus) {
        this.showPrecisionIconStatus = showPrecisionIconStatus;
    }

    /**
     * Converts distance unit from meters to the preferred unit.
     *
     * @param distance distance in meters.
     * @return Formatted distance string.
     */
    @NonNull
    public String calculateDistanceAccordingToSettingUnit(double distance) {
        if (distanceUnit == null) {
            distanceUnit = "m";
        }

        switch (distanceUnit) {
            case "m":
                return String.format("%.2f m", distance);
            case "km":
                return String.format("%.3f km", distance / 1000);
            default:
                return String.format("%.2f ?", distance);
        }
    }

    /**
     * Converts area unit from square meters to the preferred unit.
     *
     * @param areaInSquareMeters area in square meters.
     * @return Formatted area string.
     */
    @NonNull
    public String calculateAreaAccordingToSettingUnit(double areaInSquareMeters) {
        if (areaUnit == null) {
            areaUnit = "m²";
        }

        switch (areaUnit) {
            case "m²":
                return String.format("%.2f m²", areaInSquareMeters);
            case "ar":
                return String.format("%.2f ar", areaInSquareMeters / 100);
            case "ha":
                return String.format("%.2f ha", areaInSquareMeters / 10_000);
            case "km²":
                return String.format("%.2f km²", areaInSquareMeters / 1_000_000);
            default:
                return "n/a";
        }
    }

    /**
     * Sets distance unit.
     *
     * @param distanceUnit distance unit string.
     */
    public void setDistanceUnit(@Nullable String distanceUnit) {
        if (distanceUnit != null && !distanceUnit.isEmpty()) {
            this.distanceUnit = distanceUnit;
        }
    }

    /**
     * Sets area unit.
     *
     * @param areaUnit area unit string.
     */
    public void setAreaUnit(@Nullable String areaUnit) {
        if (areaUnit != null && !areaUnit.isEmpty()) {
            this.areaUnit = areaUnit;
        }
    }
}
