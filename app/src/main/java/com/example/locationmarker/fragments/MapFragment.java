package com.example.locationmarker.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.locationmarker.R;
import com.example.locationmarker.controls.GpsPrecisionIconController;
import com.example.locationmarker.dialog.InputDialog;
import com.example.locationmarker.markers.MarkersManager;
import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.locationmarker.constants.LocationMarkerConstants.DEFAULT_ZOOM;
import static com.example.locationmarker.constants.LocationMarkerConstants.INIT_LOCATION_LAT;
import static com.example.locationmarker.constants.LocationMarkerConstants.INIT_LOCATION_LON;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback {
    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int PERMISSION_REQUEST_CODE = 1234;

    private static Location mLastLocation = null;
    private static GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private static LinearLayout addPointLayer, saveLayer;
    private static Button addPointButton;
    private static Button stopAddingButton;
    private GpsPrecisionIconController gpsPrecisionIconController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (isServicesOK() && areGrantedPermission()) {
            initMap();
        }
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && googleMap != null) {
            resetBottomLayer();
            SurfaceManager.getInstance().reset();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(Location location) {
        gpsPrecisionIconController.update(String.format("%.02f m", location.getAccuracy()));
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
        MapFragment.googleMap = googleMap;
        initMapLayer();

        // vars
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, DEFAULT_ZOOM));
        googleMap.setPadding(0, 300, 0, 0);

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
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.d(LOG_TAG, "onSuccess: successfully got last location");
            }
            mLastLocation = location;
        });
        googleMap.setMyLocationEnabled(true);
        MarkersManager.setContext(getContext());
        MarkersManager.getInstance().setGoogleMap(googleMap);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.5f, this);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
    }

    private boolean areGrantedPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getContext(),
                        WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this.getContext(),
                            READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                }
            }
        }
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                        return;
                    }
                    initMap();
                }
            }
        }
    }

    public int adPoint() {
        if (mLastLocation == null) {
            return 0;
        }
        return SurfaceManager.getInstance().addPointToCurrentLocation(mLastLocation);
    }

    public void finish() {
        if (mLastLocation == null) {
            return;
        }
        SurfaceManager.getInstance().finish();
    }

    public void resetBottomLayer() {
        addPointLayer.setVisibility(View.VISIBLE);
        saveLayer.setVisibility(View.INVISIBLE);

        addPointButton.setVisibility(View.VISIBLE);
        stopAddingButton.setVisibility(View.INVISIBLE);
    }

    public void hideAddLayerAndMoveToSurface(Surface surface) {
        addPointLayer.setVisibility(View.INVISIBLE);
        LatLng surfaceCenter = SurfaceManager.getInstance().getSurfaceCenterPoint(surface.convertToLatLngList());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(surfaceCenter, DEFAULT_ZOOM));
    }

    private boolean isServicesOK() {
        Log.d(LOG_TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map requests
            Log.d(LOG_TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(LOG_TAG, "isServicesOK: an error occured but we can fix it");
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void initMap() {
        // initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    mLastLocation = loc;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), DEFAULT_ZOOM));
                });
        supportMapFragment.getMapAsync(this);
    }

    private void initMapLayer() {
        // initialize custom buttons and layers
        addPointLayer = getActivity().findViewById(R.id.addPointEndLayer);
        saveLayer = getActivity().findViewById(R.id.saveResetLayer);

        addPointButton = getActivity().findViewById(R.id.addPointButton);
        stopAddingButton = getActivity().findViewById(R.id.stopAddingButton);
        Button saveButton = getActivity().findViewById(R.id.saveButton);
        Button resetButton = getActivity().findViewById(R.id.resetButton);
        gpsPrecisionIconController = new GpsPrecisionIconController(getContext(), getActivity());
        resetBottomLayer();


        addPointButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickAddPointButton: button clicked");
            int points = adPoint();

            if (points == 3) {
                stopAddingButton.setVisibility(View.VISIBLE);
            }
        });

        stopAddingButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickEndButton: button clicked");
            finish();

            addPointLayer.setVisibility(View.INVISIBLE);
            saveLayer.setVisibility(View.VISIBLE);
        });

        resetButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickResetButton: button clicked");
            SurfaceManager.getInstance().reset();
            resetBottomLayer();
        });

        saveButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickSaveButton: button clicked");
            InputDialog.getInstance().setOnDialogTextInputListener((pos, text) -> {
                SurfaceManager.getInstance().storeNewSurface(text);
                resetBottomLayer();
            });
            int itemPosition = SurfaceManager.getInstance().getSurfaces().size();
            InputDialog.getInstance().startAlertDialog(itemPosition);
        });
    }
}
