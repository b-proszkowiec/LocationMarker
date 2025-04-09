package com.bpr.pecka.fragments;

import static com.bpr.pecka.constants.LocationMarkerConstants.DEFAULT_ZOOM;
import static com.bpr.pecka.constants.LocationMarkerConstants.INIT_LOCATION_LAT;
import static com.bpr.pecka.constants.LocationMarkerConstants.INIT_LOCATION_LON;
import static com.bpr.pecka.constants.LocationMarkerConstants.LAST_KNOWN_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bpr.pecka.R;
import com.bpr.pecka.controls.GpsPrecisionIconController;
import com.bpr.pecka.dialog.ConfirmationDialog;
import com.bpr.pecka.dialog.InputDialog;
import com.bpr.pecka.event.IMapMarker;
import com.bpr.pecka.storage.SurfaceRepository;
import com.bpr.pecka.surface.EditSurface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, IMapMarker {
    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int PERMISSION_REQUEST_CODE = 1234;

    private static Location mLastLocation = null;
    private static GoogleMap googleMap;
    private LinearLayout addPointLayer, saveLayer;
    private Button addPointButton;
    private Button stopAddingButton;
    private FusedLocationProviderClient fusedLocationClient;
    private GpsPrecisionIconController gpsPrecisionIconController;
    private Marker tempPositionMarker;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(LOG_TAG, "Updated location: " + location.getLatitude() + ", " + location.getLongitude());
                gpsPrecisionIconController.update(location.getAccuracy());
                mLastLocation = location;

                if (tempPositionMarker != null) {
                    tempPositionMarker.remove();
                }

                tempPositionMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(LAST_KNOWN_LOCATION)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.temp_location_point)));
            }
        }
    };
    private EditSurface editSurface;

    /**
     * Move bottom layer into adding points mode.
     * If total amounts of points is equal or grater than 3, show also 'END' button
     * to let the user to break and save working surface.
     *
     * @param markers total amount of already added points.
     */
    public void updateBottomLayer(int markers) {
        addPointLayer.setVisibility(View.VISIBLE);
        saveLayer.setVisibility(View.INVISIBLE);
        addPointButton.setVisibility(View.VISIBLE);
        stopAddingButton.setVisibility(View.VISIBLE);

        if (markers < 3) {
            stopAddingButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initialize view
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (isServicesOK() && areGrantedPermission()) {
            initMap();
        }
        return view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onLocationChanged(Location location) {
        gpsPrecisionIconController.update(location.getAccuracy());
        Log.d(LOG_TAG, "onLocationChanged: location has changed");
        mLastLocation = location;

        if (tempPositionMarker != null) {
            tempPositionMarker.remove();
        }

        tempPositionMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(LAST_KNOWN_LOCATION)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.temp_location_point)));
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(getContext(), "GPS has been enabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getContext(), "GPS has been disabled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        editSurface = new EditSurface(requireActivity(), googleMap);
        LatLng initLocation = new LatLng(INIT_LOCATION_LAT, INIT_LOCATION_LON);
        MapFragment.googleMap = googleMap;
        initMapLayer();

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, DEFAULT_ZOOM));
        googleMap.setPadding(0, 300, 0, 0);

        Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_TAG, "The required permissions have not been granted.");
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(1000)
                .build();

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.d(LOG_TAG, "onSuccess: got last location");
                mLastLocation = location;
            } else {
                Log.d(LOG_TAG, "onSuccess: last location was null");
            }
        });

        googleMap.setMyLocationEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        googleMap.setOnMarkerClickListener(marker -> {
            if (!LAST_KNOWN_LOCATION.equals(marker.getTitle())) {
                Context context = requireContext();
                Projection projection = googleMap.getProjection();
                LatLng markerLocation = marker.getPosition();
                Point screenPosition = projection.toScreenLocation(markerLocation);

                Activity activity = requireActivity();
                ViewGroup rootView = activity.findViewById(android.R.id.content);

                View transparentView = new View(context);
                transparentView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
                transparentView.setX(screenPosition.x);
                transparentView.setY(screenPosition.y);
                rootView.addView(transparentView);

                PopupMenu popupMenu = new PopupMenu(context, transparentView);
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (Objects.equals(item.getTitle(), context.getString(R.string.marker_delete_popup))) {
                        ConfirmationDialog.show(
                                getContext(),
                                "Are you sure?",
                                () -> editSurface.removeMapMarker(marker)
                        );
                    }
                    return false;
                });

                popupMenu.inflate(R.menu.marker_popup_menu);
                popupMenu.show();
                popupMenu.setOnDismissListener(menu -> rootView.removeView(transparentView));
            }
            return true;
        });
    }

    private boolean areGrantedPermission() {
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(requireContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
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
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    return;
                }
                initMap();
            }
        }
    }

    /**
     * Add last location point to a working surface.
     *
     * @return amount of points in a working surface.
     */
    public int adPoint() {
        if (mLastLocation == null) {
            return 0;
        }
        return editSurface.addPointToWorkingSurface(mLastLocation);
    }

    /**
     * Stop adding points to a working surface.
     */
    public void finish() {
        if (mLastLocation == null) {
            return;
        }
        editSurface.finish();
    }

    /**
     * Reset bottom layer to initial values.
     * This will make only 'ADD POINT' button visible.
     */
    public void resetBottomLayer() {
        addPointLayer.setVisibility(View.VISIBLE);
        saveLayer.setVisibility(View.INVISIBLE);

        addPointButton.setVisibility(View.VISIBLE);
        stopAddingButton.setVisibility(View.INVISIBLE);
    }

    private boolean isServicesOK() {
        Log.d(LOG_TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(LOG_TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(LOG_TAG, "isServicesOK: an error ocurred but we can fix it");
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Initializes map. This should be done after grant proper permissions.
     */
    public void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map_fragment);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        mLastLocation = loc;
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        turnOnLocationAlert();
                    }
                });
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
    }

    private void turnOnLocationAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Localization turned off");
        alertDialog.setMessage("Localization is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            requireContext().startActivity(intent);
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void initMapLayer() {
        Activity activity = requireActivity();
        // initialize custom buttons and layers
        addPointLayer = activity.findViewById(R.id.addPointEndLayer);
        saveLayer = activity.findViewById(R.id.saveResetLayer);

        addPointButton = activity.findViewById(R.id.addPointButton);
        stopAddingButton = activity.findViewById(R.id.stopAddingButton);
        Button saveButton = activity.findViewById(R.id.saveButton);
        Button resetButton = activity.findViewById(R.id.resetButton);
        gpsPrecisionIconController = new GpsPrecisionIconController(activity);
        resetBottomLayer();


        addPointButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickAddPointButton: button clicked");
            updateBottomLayer(adPoint());
        });

        stopAddingButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickEndButton: button clicked");
            ConfirmationDialog.show(getContext(), "Are you sure?", () -> {
                finish();
                addPointLayer.setVisibility(View.INVISIBLE);
                saveLayer.setVisibility(View.VISIBLE);
            });
        });

        resetButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickResetButton: button clicked");
            editSurface.reset();
            resetBottomLayer();
        });

        saveButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "onClickSaveButton: button clicked");
            InputDialog.getInstance().setOnDialogTextInputListener((pos, text) -> {
               boolean alreadyExist = SurfaceRepository.getSurfaces().stream()
                       .anyMatch(p -> p.getName().equals(text));
               if (alreadyExist) {
                   Toast.makeText(getContext(), "This name already exist!", Toast.LENGTH_LONG).show();
                } else {
                   editSurface.storeNewSurface(text);
                   resetBottomLayer();
               }
            });
            int itemPosition = SurfaceRepository.getSurfaces().size();
            InputDialog.getInstance().startAlertDialog(itemPosition, "");
        });
    }

    @Override
    public void onLocationMarkerDelete(int markers) {
        updateBottomLayer(markers);
    }
}
