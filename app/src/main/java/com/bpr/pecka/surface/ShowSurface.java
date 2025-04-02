package com.bpr.pecka.surface;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public class ShowSurface {
    private Context context;
    private GoogleMap googleMap;

    public ShowSurface(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
    }
}
