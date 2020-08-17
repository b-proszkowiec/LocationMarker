package com.example.locationmarker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//* osmd
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "MapActivity";
    private static final double TEST_LOCATION_LAT = 50.06166667;      // rynek w Krakowie
    private static final double TEST_LOCATION_LON = 19.93722222;
    private static final float DEFAULT_ZOOM = 15f;

    // vars
    private MapView mMap = null;
    private Location mLocation = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        initMap();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = (MapView) findViewById(R.id.map);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);

        goToTestLocation();
    }

    private void goToTestLocation() {
        moveCamera(new GeoPoint(TEST_LOCATION_LAT, TEST_LOCATION_LON), DEFAULT_ZOOM);
        addMarker(new GeoPoint(TEST_LOCATION_LAT, TEST_LOCATION_LON));
    }

    private void moveCamera(GeoPoint position, float zoom) {
        mMap.getController().setZoom(zoom);
        mMap.getController().animateTo(position);
    }

    private void moveCamera(Location mLocation, float zoom) {
        GeoPoint position;
        if (mLocation == null) {
            position = new GeoPoint(TEST_LOCATION_LAT, TEST_LOCATION_LON);
        } else {
            position = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
        }
        moveCamera(position, zoom);
    }

    private void markPosition(GeoPoint startPoint) {

    }

    private void addMarker(GeoPoint startPoint) {
        Marker startMarker = new Marker(mMap);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMap.getOverlays().add(startMarker);
    }

    public void centerButtonOnClick(View view) {
        moveCamera(mLocation, (float) mMap.getZoomLevelDouble());
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        if (mMap != null) {
            mMap.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (mMap != null) {
            mMap.onPause();  //needed for compass, my location overlays, v6.0.0 and up
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.toString());
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
