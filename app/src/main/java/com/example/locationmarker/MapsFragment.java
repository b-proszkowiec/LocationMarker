package com.example.locationmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.locationmarker.markers.MarkersContainer;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import android.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {
    private static final String LOG_TAG = "MapsFragment";
    private static final float DEFAULT_ZOOM = 17f;
    private static final double INIT_LOCATION_LAT = 52.22514419;      // Ordona Warszawa
    private static final double INIT_LOCATION_LON = 20.95346435;

    // vars
    private GoogleMap mMap;
    private static Location mLastLocation = null;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private MarkersContainer markersContainer;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // initialize view
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        // initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        String text = "Accurancy : " + location.getAccuracy() + "m";

        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "onLocationChanged: location has changed");
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
        LatLng initLocation = new LatLng(INIT_LOCATION_LAT, INIT_LOCATION_LON);

        if (googleMap == mMap) {
            return;
        }

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, DEFAULT_ZOOM));

        Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d(LOG_TAG, "onSuccess: successfully got last location");
                }
                mLastLocation = location;
            }
        });
        mMap.setMyLocationEnabled(true);
        MarkersContainer.getInstance().setContext(getContext());
        MarkersContainer.getInstance().setMap(mMap);
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,0.5f, this);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
    }

    public void adPoint() {
        if (mLastLocation == null) {
            return;
        }
        SurfaceManager.getInstance().addPointToCurrentLocation(mLastLocation);
    }
}
