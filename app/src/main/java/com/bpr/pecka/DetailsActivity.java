package com.bpr.pecka;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bpr.pecka.surface.LocationPoint;

public class DetailsActivity extends AppCompatActivity {

    private TextView latitudeValue;
    private TextView longitudeValue;
    private TextView accuracyValue;
    private TextView idValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        idValue = findViewById(R.id.id_value);
        latitudeValue = findViewById(R.id.latitude_value);
        longitudeValue = findViewById(R.id.longitude_value);
        accuracyValue = findViewById(R.id.accuracy_value);
        TextView toolbarTextView = findViewById(R.id.toolbar_textView);
        toolbarTextView.setText(R.string.ic_details_text);
        LocationPoint locationPoint = (LocationPoint) getIntent().getSerializableExtra(LocationPoint.class.getSimpleName());
        if (locationPoint != null) {
            fillDetailsData(locationPoint);
        }
    }

    @SuppressLint("DefaultLocale")
    private void fillDetailsData(LocationPoint locationPoint) {
        idValue.setText(String.format("%s", locationPoint.getOrderNumber()));
        latitudeValue.setText(String.format("%f", locationPoint.getLatLng().latitude));
        longitudeValue.setText(String.format("%f", locationPoint.getLatLng().longitude));
        accuracyValue.setText(String.format("%f", locationPoint.getAccuracy()));
    }
}
