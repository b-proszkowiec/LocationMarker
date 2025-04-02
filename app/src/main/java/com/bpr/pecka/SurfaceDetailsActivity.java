package com.bpr.pecka;

import static com.bpr.pecka.constants.LocationMarkerConstants.DEFAULT_ZOOM;
import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATIONS_ITEM_SELECTED;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bpr.pecka.surface.Surface;
import com.bpr.pecka.surface.SurfaceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;



public class SurfaceDetailsActivity extends AppCompatActivity  implements OnMapReadyCallback {
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
            this.surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
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

        SurfaceManager.getInstance().setLastViewedSurface(surface);
        SurfaceManager.getInstance().refreshView(true, surface);

        LatLng surfaceCenter = SurfaceManager.getInstance().getSurfaceCenterPoint(surface.convertToLatLngList());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surfaceCenter, DEFAULT_ZOOM));
    }
}
