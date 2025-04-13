package com.bpr.pecka;

import static com.bpr.pecka.constants.LocationMarkerConstants.LOCATION_POINT;
import static com.bpr.pecka.constants.LocationMarkerConstants.SURFACE_NAME;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bpr.pecka.settings.LocaleHelper;
import com.bpr.pecka.surface.LocationPoint;

import java.util.Locale;

public class LocationDetailsActivity extends AppCompatActivity {
    private TextView surfaceNameValue;
    private TextView latitudeValue;
    private TextView longitudeValue;
    private TextView altitudeValue;
    private TextView accuracyValue;
    private TextView referenceValue;
    private TextView idValue;
    private String positiveText;
    private String negativeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        positiveText= this.getBaseContext().getString(R.string.positive_text);
        negativeText= this.getBaseContext().getString(R.string.negative_text);

        ImageButton button = this.findViewById(R.id.btn_close);
        button.setOnClickListener(v -> finish());
        surfaceNameValue = findViewById(R.id.surface_name_value);
        idValue = findViewById(R.id.id_value);
        latitudeValue = findViewById(R.id.latitude_value);
        longitudeValue = findViewById(R.id.longitude_value);
        altitudeValue = findViewById(R.id.altitude_value);
        accuracyValue = findViewById(R.id.accuracy_value);
        referenceValue = findViewById(R.id.reference_value);
        Button referenceChange = findViewById(R.id.switch_reference);

        if (getIntent().hasExtra(LOCATION_POINT) && getIntent().hasExtra(SURFACE_NAME)) {
            LocationPoint locationPoint = (LocationPoint) getIntent()
                    .getSerializableExtra(LOCATION_POINT);

            referenceChange.setOnClickListener(v -> {
                assert locationPoint != null;
                boolean isLocationReference = locationPoint.isReference();
                locationPoint.setReference(!isLocationReference);
                referenceValue.setText(locationPoint.isReference() ? positiveText : negativeText);
            });

            String surfaceName = getIntent().getStringExtra(SURFACE_NAME);
            assert locationPoint != null;
            fillDetailsData(locationPoint, surfaceName);
        }
    }

    private void fillDetailsData(LocationPoint locationPoint, String surfaceName) {
        surfaceNameValue.setText(surfaceName);
        idValue.setText(String.format("%s", locationPoint.getOrderNumber()));
        latitudeValue.setText(String.format(Locale.getDefault(), "%f", locationPoint.getLatLng().latitude));
        longitudeValue.setText(String.format(Locale.getDefault(), "%f", locationPoint.getLatLng().longitude));
        altitudeValue.setText(String.format(Locale.getDefault(), "%f", locationPoint.getAltitude()));
        accuracyValue.setText(String.format(Locale.getDefault(), "%.2f m", locationPoint.getAccuracy()));
        referenceValue.setText(locationPoint.isReference() ? positiveText : negativeText);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
}
