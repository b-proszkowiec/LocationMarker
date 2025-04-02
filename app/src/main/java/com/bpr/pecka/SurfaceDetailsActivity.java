package com.bpr.pecka;

import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATIONS_ITEM_SELECTED;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bpr.pecka.surface.LocationPoint;
import com.bpr.pecka.surface.Surface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Optional;


public class SurfaceDetailsActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private static final String LOG_TAG = SurfaceDetailsActivity.class.getSimpleName();

    private Surface surface;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        ImageButton button = this.findViewById(R.id.btn_close);
        button.setOnClickListener(v -> finish());

        TextView surfaceNameTextView = this.findViewById(R.id.surface_name);
        if (getIntent().hasExtra(LOCATIONS_ITEM_SELECTED)) {
            int itemPosition = getIntent().getIntExtra(LOCATIONS_ITEM_SELECTED, 0);
//            this.surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
            surfaceNameTextView.setText(this.surface.getName());
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.show_surface_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);

//        SurfaceManager.getInstance().setLastViewedSurface(surface);
//        SurfaceManager.getInstance().refreshView(true, surface);
//
//        LatLng surfaceCenter = SurfaceManager.getInstance().getSurfaceCenterPoint(surface.convertToLatLngList());
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surfaceCenter, DEFAULT_ZOOM));

        mMap.setOnMarkerClickListener(marker -> {
            try {
                int id = parseInt(marker.getTitle());
                Optional<LocationPoint> locationPoint = surface.getPoints().stream()
                        .filter(p -> p.getOrderNumber() == id)
                        .findFirst();

                if (locationPoint.isPresent()) {
                    showDetailsLayout(locationPoint.get());
                    return true;
                }
                Log.e(LOG_TAG, "Unable to recognize LocationPoint object of selected marker!");
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
            }
            return false;
        });
    }

    private void showDetailsLayout(LocationPoint locationPoint) {
        Context context = getApplicationContext();
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.putExtra("LocationPoint", locationPoint);
        context.startActivity(intent);
    }
}
