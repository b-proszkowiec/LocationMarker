package com.example.locationmarker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.locationmarker.markers.MarkersContainer;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;


import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback {
    private static final String LOG_TAG = "MapsFragment";
    private static final float DEFAULT_ZOOM = 17f;
    private static final double INIT_LOCATION_LAT = 52.22514419;      // Ordona Warszawa
    private static final double INIT_LOCATION_LON = 20.95346435;

    private static Location mLastLocation = null;
    private FusedLocationProviderClient fusedLocationClient;
    private View view;

    private static String alertDialogInputText;
    private static LinearLayout addPointLayer, saveLayer;
    private static Button addPointButton, stopAddingButton, saveButton, resetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // initialize view
        view = inflater.inflate(R.layout.fragment_map, container, false);

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
        initMapLayer();

        // vars
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, DEFAULT_ZOOM));

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
        googleMap.setMyLocationEnabled(true);
        MarkersContainer.setContext(getContext());
        MarkersContainer.getInstance().setMap(googleMap);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L,0.5f, this);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
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

    public static void resetBottomLayer() {
        addPointLayer.setVisibility(View.VISIBLE);
        saveLayer.setVisibility(View.INVISIBLE);

        addPointButton.setVisibility(View.VISIBLE);
        stopAddingButton.setVisibility(View.INVISIBLE);
    }

    private void initMapLayer() {

        // initialize custom buttons and layers
        addPointLayer = getActivity().findViewById(R.id.addPointEndLayer);
        saveLayer = getActivity().findViewById(R.id.saveResetLayer);

        addPointButton = getActivity().findViewById(R.id.addPointButton);
        stopAddingButton = getActivity().findViewById(R.id.stopAddingButton);
        saveButton = getActivity().findViewById(R.id.saveButton);
        resetButton = getActivity().findViewById(R.id.resetButton);
        resetBottomLayer();

        addPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClickAddPointButton: button clicked");
                int points = adPoint();

                if (points == 3) {
                    stopAddingButton.setVisibility(View.VISIBLE);
                }
            }
        });

        stopAddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClickEndButton: button clicked");
                finish();

                addPointLayer.setVisibility(View.INVISIBLE);
                saveLayer.setVisibility(View.VISIBLE);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClickResetButton: button clicked");
                SurfaceManager.getInstance().reset();
                resetBottomLayer();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClickSaveButton: button clicked");

                Runnable alertDialogRunnable = new Runnable() {
                    @Override
                    public void run() {
                        SurfaceManager.getInstance().save(getContext(), alertDialogInputText);
                    }
                };
                userInput(alertDialogRunnable);
                resetBottomLayer();
            }
        });
    }

    private void userInput(final Runnable func) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());

        aBuilder.setTitle("Give a name for new area:");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        aBuilder.setView(input);

        aBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogInputText = input.getText().toString();
                func.run();
            }
        });
        aBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                alertDialogInputText = "";
            }
        });
        aBuilder.show();
    }
}