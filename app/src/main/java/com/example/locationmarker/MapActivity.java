package com.example.locationmarker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//* osmd
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "MapActivity";

    private static final float DEFAULT_ZOOM = 15f;

    // vars
    private static Boolean mLocationPermissionGranted = false;
    private MapView mMap = null;
    private LocationManager mLocationManager = null;
    private Location mLocation = null;
    private float mCurrentZoom = DEFAULT_ZOOM;
    private MyLocationNewOverlay mMyLocationOverlay;

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
        GeoPoint startPoint = new GeoPoint(50.06166667, 19.93722222);   // rynek w Kraowie
        moveCamera(startPoint, DEFAULT_ZOOM);
        addMarker(startPoint);
    }

    private void moveCamera(GeoPoint geoLocation, float zoom) {
        mMap.getController().setZoom(zoom);
        mMap.getController().animateTo(geoLocation);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (Exception ex) {
            Log.d(TAG, "getDeviceLocation: exception occured " + ex.toString());
            //do something useful here
        }
    }

    private void markPosition(GeoPoint startPoint) {

    }

    private void addMarker(GeoPoint startPoint) {
        Marker startMarker = new Marker(mMap);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMap.getOverlays().add(startMarker);
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
        Log.d(TAG, "onLocationChanged: provider location changed");
        mLocation = location;

        Log.d(TAG, "onLocationChanged: " + location.toString());
        moveCamera(new GeoPoint(location.getLatitude(), location.getLongitude()), (float) mMap.getZoomLevelDouble());
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
