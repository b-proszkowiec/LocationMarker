package com.example.locationmarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.locationmarker.dialog.InputDialog;
import com.example.locationmarker.surface.Surface;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // vars
    private final MapFragment mapFragment = new MapFragment();
    private final ItemFragment itemFragment = new ItemFragment();
    private final SettingsFragment settingFragment = new SettingsFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment = mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServicesOK()) {
            getLocationPermission();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager.beginTransaction().add(R.id.fragment_container, settingFragment, "3").hide(settingFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, itemFragment, "2").hide(itemFragment).commit();
        //fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment, "1").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mapsFragment:
                    fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                    mapFragment.onHiddenChanged(false);
                    activeFragment = mapFragment;
                    return true;

                case R.id.itemFragment:
                    fragmentManager.beginTransaction().hide(activeFragment).show(itemFragment).commit();
                    activeFragment = itemFragment;
                    return true;

                case R.id.settingsFragment:
                    fragmentManager.beginTransaction().hide(activeFragment).show(settingFragment).commit();
                    activeFragment = settingFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        SurfaceManager.getInstance().setContext(getApplicationContext());
        SurfaceManager.getInstance().restoreSavedSurfaces();
        InputDialog.getInstance().setContext(this);

        itemFragment.setOnLocationItemClickListener(new ItemFragment.OnLocationItemClickListener() {
            @Override
            public void onLocationItemClickListener(int itemPosition) {
                fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                activeFragment = mapFragment;
                Surface surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
                SurfaceManager.getInstance().refreshView(true, surface);
                mapFragment.hideAddLayerAndMoveToSurface(surface);
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(LOG_TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map requests
            Log.d(LOG_TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(LOG_TAG, "isServicesOK: an error occured but we can fix it");
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(LOG_TAG, "onRequestPermissionsResult: called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Log.d(LOG_TAG, "onRequestPermissionsResult: permission failed");
                        return;
                    }
                }
                Log.d(LOG_TAG, "onRequestPermissionsResult: permission granted");
            }
        }
    }

    private void getLocationPermission() {
        Log.d(LOG_TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION, WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
