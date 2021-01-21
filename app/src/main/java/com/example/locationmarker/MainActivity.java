package com.example.locationmarker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    // vars
    private static Boolean mLocationPermissionGranted = false;
    private MapsFragment mapFragment;
    private LinearLayout buttonsLayer1, buttonsLayer2;
    private String alertDialogInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = new MapsFragment();

        if (isServicesOK()) {
            getLocationPermission();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.mapsFragment:
                        selectedFragment = mapFragment;
                        break;
                    case R.id.itemFragment:
                        selectedFragment = new ItemFragment();
                        break;
                    case R.id.settingsFragment:
                        selectedFragment = new SettingsFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // initialize custom buttons and layers
        buttonsLayer1 = findViewById(R.id.AddPointEndLayer);
        buttonsLayer2 = findViewById(R.id.saveResetLayer);
        resetBottomLayer();
        SurfaceManager.getInstance().restoreSavedSurfaces(this.getApplicationContext());
    }

    private void mapInit() {
        mLocationPermissionGranted = true;
    }

    private void resetBottomLayer() {
        buttonsLayer1.setVisibility(View.VISIBLE);
        buttonsLayer2.setVisibility(View.INVISIBLE);

        findViewById(R.id.addPointButton).setVisibility(View.VISIBLE);
        findViewById(R.id.stopAddingButton).setVisibility(View.INVISIBLE);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and user can make map requests
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mapInit();
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                }
            }
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION, WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    mapInit();
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

    public void onClickAddPointButton(View view) {
        Log.d(TAG, "onClickAddPointButton: button clicked");
        int points = mapFragment.adPoint();

        if (points == 3) {
            findViewById(R.id.stopAddingButton).setVisibility(View.VISIBLE);
        }
    }

    public void onClickEndButton(View view) {
        Log.d(TAG, "onClickEndButton: button clicked");
        mapFragment.finish();

        buttonsLayer1.setVisibility(View.INVISIBLE);
        buttonsLayer2.setVisibility(View.VISIBLE);
    }

    public void onClickResetButton(View view) {
        Log.d(TAG, "onClickResetButton: button clicked");
        SurfaceManager.getInstance().reset();
        resetBottomLayer();
    }

    public void onClickSaveButton(View view) throws InterruptedException {
        Log.d(TAG, "onClickSaveButton: button clicked");

        Runnable alertDialogRunnable = new Runnable() {
            @Override
            public void run() {
                SurfaceManager.getInstance().save(getApplicationContext(), alertDialogInputText);
            }
        };
        userInput("Set new area name:", alertDialogRunnable);
        resetBottomLayer();
    }

    private void userInput(String sTitle, final Runnable func) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);

        aBuilder.setTitle(sTitle);
        final EditText input = new EditText(this);
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
