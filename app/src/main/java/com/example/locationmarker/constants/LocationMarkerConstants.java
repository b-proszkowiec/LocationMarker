package com.example.locationmarker.constants;

public final class LocationMarkerConstants {
    public static final float DEFAULT_ZOOM = 19f;
    public static final double INIT_LOCATION_LAT = 50.06167366350375;      // rynek w Krakowie
    public static final double INIT_LOCATION_LON = 19.93725953201794;

    public static class GpsPrecisionIconControllerConstants {
        public static final int NO_LOCATION_UPDATE_TIMEOUT = 10000;
    }

    public static class DataStorageConstants {
        public static final String STORAGE_FILE_NAME = "location_marker_temp.txt";
    }
}
