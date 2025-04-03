package com.bpr.pecka.surface;

import static java.lang.Integer.parseInt;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bpr.pecka.R;
import com.bpr.pecka.storage.SurfaceRepository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Optional;

public class EditSurface extends MapSurface {

    private static final String LOG_TAG = EditSurface.class.getSimpleName();
    private static final String TEMP_NAME = "Name";
    private static final double MINIMAL_DISTANCE = 0.5;
    private final Button surfaceNameButton;
    private Surface workingSurface = new Surface(TEMP_NAME);

    public EditSurface(Activity activity, GoogleMap googleMap) {
        super(activity.getApplicationContext(), googleMap);
        surfaceNameButton = activity.findViewById(R.id.surfaceNameButton);
    }

    /**
     * Add new location point to a working surface.
     *
     * @param location new point location
     * @return amount of location points in current surface
     */
    public int addPointToWorkingSurface(Location location) {
        if (!workingSurface.getPoints().isEmpty()) {
            LocationPoint lastLocation = workingSurface.getPoints().get(workingSurface.getPoints().size() - 1);
            double distance = calculateDistanceBetweenLocations(lastLocation.getLocation(), location);
            if (distance < MINIMAL_DISTANCE) {
                Toast.makeText(this.context,
                                String.format("Minimal is %.1f m", MINIMAL_DISTANCE),
                                Toast.LENGTH_SHORT)
                        .show();
                return workingSurface.getPoints().size();
            }
        }
        workingSurface.addPointToSurface(location);
        refreshView(false, workingSurface);
        return workingSurface.getPoints().size();
    }


    /**
     * Refresh view of the surface on the map.
     *
     * @param isAddingProcessFinished determines whether add points to surface is finished.
     * @param surface                 current surface.
     */
    public void refreshView(boolean isAddingProcessFinished, Surface surface) {
        googleMap.clear();
        if (isAddingProcessFinished) {
            showSurfaceOnMap(surface);
            surfaceNameButton.setVisibility(View.VISIBLE);
            surfaceNameButton.setText(surface.getName());
        } else {
            showLocationMarkerOnMap(surface.getPoints());
            if (surface.getPoints().size() > 1) {
                drawPolyline(false, workingSurface);
            }
        }
    }

    /**
     * Stop adding new points to a working surface and create the polygon on the map.
     * This will be happen by joining the last location point with the first one.
     */
    public void finish() {
        refreshView(true, workingSurface);
    }


    /**
     * Stop adding new points to a working surface and reset the process.
     */
    public void reset() {
        workingSurface = new Surface(TEMP_NAME);
        refreshView(false, workingSurface);
    }


    /**
     * Add new created surface to surfaces list.
     *
     * @param name name of the surface.
     */
    public void storeNewSurface(String name) {
        workingSurface.setName(name);
        SurfaceRepository.addSurface(workingSurface);

        workingSurface = new Surface(TEMP_NAME);
        refreshView(false, workingSurface);
    }

    /**
     * Remove selected marker from the map.
     *
     * @param marker marker to remove.
     */
    public void removeMapMarker(@NonNull Marker marker) {
        List<LocationPoint> locationPoints = workingSurface.getPoints();
        try {
            final int id = parseInt(marker.getTitle());
            Optional<LocationPoint> markerLocationPoint = locationPoints.stream()
                    .filter(p -> p.getOrderNumber() == id)
                    .findFirst();
            if (markerLocationPoint.isPresent()) {
                workingSurface.getPoints().remove(markerLocationPoint.get());
                refreshView(false, workingSurface);
            }
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException occurred while parsing: " + marker.getTitle());
        }

    }
}
