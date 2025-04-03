package com.bpr.pecka.constants;

public final class LocationMarkerConstants {

    public static final float DEFAULT_ZOOM = 19f;
    public static final double INIT_LOCATION_LAT = 50.06167366350375;      // rynek w Krakowie
    public static final double INIT_LOCATION_LON = 19.93725953201794;
    public static final String LOCATIONS_ITEM_SELECTED = "LOCATIONS_ITEM_SELECTED";
    public static final String LAST_KNOWN_LOCATION = "LAST_KNOWN_LOCATION";
    public static final String LOCATION_POINT = "LOCATION_POINT";
    public static final String SURFACE_NAME = "SURFACE_NAME";

    private LocationMarkerConstants() {
    }

    public static class GpsPrecisionIconControllerConstants {
        public static final int NO_LOCATION_UPDATE_TIMEOUT = 10000;
    }

    public static class DataStorageConstants {
        public static final String STORAGE_FILE_NAME = "location_marker_temp.txt";
    }
}
