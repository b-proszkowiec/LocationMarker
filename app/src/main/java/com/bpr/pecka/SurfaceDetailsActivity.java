package com.bpr.pecka;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SurfaceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        
    }
}
