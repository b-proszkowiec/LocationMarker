package com.example.locationmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.locationmarker.markers.MarkersContainer;
import com.example.locationmarker.markers.ShowPopUp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final double INIT_LOCATION_LAT = 52.22514419;      // Ordona Warszawa
    private static final double INIT_LOCATION_LON = 20.95346435;
    private static final float DEFAULT_ZOOM = 17f;

    // vars
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location mLastLocation = null;
    private MarkersContainer markersContainer;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        String text = "Accurancy : " + location.getAccuracy() + "m";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLocationChanged: location has changed");
        mLastLocation = location;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        LatLng krakow = new LatLng(INIT_LOCATION_LAT, INIT_LOCATION_LON);

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(krakow, DEFAULT_ZOOM));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, "onSuccess: successfully got last location");
                            // Logic to handle location object
                            mLastLocation = location;
                        }
                    }
                });

        mMap.setMyLocationEnabled(true);
        markersContainer.getInstance().setContext(this);
        markersContainer.getInstance().setMap(mMap);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,0.5f, this);
    }

    public void onClickAddPointButton(View view) {
        Log.d(TAG, "addPointButtonOnClickListener: button clicked");

        markersContainer.getInstance().addMarker(mLastLocation);
        markersContainer.getInstance().drawPolyline();
    }
    
    public void onClickTestButton(View view) {
        Log.d(TAG, "onClickTestButton: button clicked");
        markerTest();
    }

    private void markerTest() {

        markersContainer.getInstance().clear();
        markersContainer.getInstance().addMarker(getTestLocation(52.22526819, 20.95346435));
        markersContainer.getInstance().addMarker(getTestLocation(52.22526819, 20.95376435));
        markersContainer.getInstance().addMarker(getTestLocation(52.22566819, 20.95346435));
        //markersContainer.getInstance().addMarker(getTestLocation(52.22566819, 20.95376436));
        //markersContainer.getInstance().addMarker(getTestLocation(52.22516819, 20.95371436));

        Toast.makeText(this, "Surface is equal to: " + markersContainer.getInstance().computeArea(), Toast.LENGTH_SHORT).show();
    }

    private Location getTestLocation(double latitude, double longitude) {
        Location loc = new Location(mLastLocation);
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc;
    }
}
