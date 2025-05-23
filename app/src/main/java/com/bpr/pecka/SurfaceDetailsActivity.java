package com.bpr.pecka;

import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATIONS_ITEM_SELECTED;
import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATION_POINT;
import static com.bpr.pecka.constants.LocationMarkerConstants.SURFACE_NAME;

import static java.lang.Integer.parseInt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bpr.pecka.settings.LocaleHelper;
import com.bpr.pecka.storage.SurfaceRepository;
import com.bpr.pecka.surface.LocationPoint;
import com.bpr.pecka.surface.MapSurface;
import com.bpr.pecka.surface.Surface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Optional;


public class SurfaceDetailsActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private static final String LOG_TAG = SurfaceDetailsActivity.class.getSimpleName();

    private Surface surface;
    private GoogleMap mMap;
    private MapSurface mapSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_details);
        ImageButton button = this.findViewById(R.id.btn_close);
        button.setOnClickListener(v -> finish());

        TextView surfaceNameTextView = this.findViewById(R.id.surface_name);
        if (getIntent().hasExtra(LOCATIONS_ITEM_SELECTED)) {
            int itemPosition = getIntent().getIntExtra(LOCATIONS_ITEM_SELECTED, 0);
            this.surface = SurfaceRepository.getSurfaces().get(itemPosition);
            surfaceNameTextView.setText(this.surface.getName());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.show_surface_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapSurface != null)
            mapSurface.showSurfaceOnMap(this.surface);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapSurface = new MapSurface(getApplicationContext(), googleMap);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mapSurface.showSurfaceOnMap(this.surface);

        mMap.setOnMarkerClickListener(marker -> {
            try {
                int id = parseInt(marker.getTitle());
                Optional<LocationPoint> locationPoint = surface.getPoints().stream()
                        .filter(p -> p.getOrderNumber() == id)
                        .findFirst();

                if (locationPoint.isPresent()) {
                    showDetailsLayout(locationPoint.get(), surface.getName(), getBaseContext());
                    return true;
                }
                Log.e(LOG_TAG, "Unable to recognize LocationPoint object of selected marker!");
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
            }
            return false;
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void showDetailsLayout(LocationPoint locationPoint, String surfaceName, Context context) {
        Intent intent = new Intent(context, LocationDetailsActivity.class);
        intent.putExtra(LOCATION_POINT, locationPoint);
        intent.putExtra(SURFACE_NAME, surfaceName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
}
