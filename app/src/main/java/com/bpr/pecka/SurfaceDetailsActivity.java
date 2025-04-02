package com.bpr.pecka;

import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATIONS_ITEM_SELECTED;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bpr.pecka.surface.Surface;
import com.bpr.pecka.surface.SurfaceManager;

public class SurfaceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        ImageButton button = this.findViewById(R.id.btn_close);
        button.setOnClickListener(v -> finish());

        TextView surfaceNameTextView = this.findViewById(R.id.surface_name);
        if (getIntent().hasExtra(LOCATIONS_ITEM_SELECTED)) {
            int itemPosition = getIntent().getIntExtra(LOCATIONS_ITEM_SELECTED, 0);
            Surface surface = SurfaceManager.getInstance().getSurfaces().get(itemPosition);
            surfaceNameTextView.setText(surface.getName());
        }



    }
}
