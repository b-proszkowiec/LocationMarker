package com.bpr.pecka.markers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.bpr.pecka.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.Objects;


public class MapMarker {

    private static final String LOG_TAG = MapMarker.class.getSimpleName();

    private GoogleMap googleMap;
    private Polyline polyline;
    private  Context context;


    private MapMarker(@NonNull Context context) {

    }



    /**
     * Sets the value of the private googleMap field to the specified.
     *
     * @param gMap specified GoogleMap value.
     */
    public void setGoogleMap(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setOnMarkerClickListener(marker -> {

            Projection projection = googleMap.getProjection();
            LatLng markerLocation = marker.getPosition();
            Point screenPosition = projection.toScreenLocation(markerLocation);

            Activity activity = (Activity) this.context;
            ViewGroup rootView = activity.findViewById(android.R.id.content);

            View transparentView = new View(this.context);
            transparentView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
            transparentView.setX(screenPosition.x);
            transparentView.setY(screenPosition.y);
            rootView.addView(transparentView);

            PopupMenu popupMenu = new PopupMenu(this.context, transparentView);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (Objects.equals(item.getTitle(), this.context.getString(R.string.marker_delete_popup))) {
//                    removeMarker(marker);
                }
                return false;
            });

            popupMenu.inflate(R.menu.marker_popup_menu);
            popupMenu.show();
            popupMenu.setOnDismissListener(menu -> rootView.removeView(transparentView));

            return true;
            }
        );
    }



}
