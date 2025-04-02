package com.bpr.pecka.surface;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

public class EditSurface extends MapSurface{

    private static final String LOG_TAG = EditSurface.class.getSimpleName();
    private static final long serialVersionUID = -5444204010422813540L;
    private static final String TEMP_NAME = "Name";

    private Surface workingSurface = new Surface(TEMP_NAME);


    public EditSurface(Context context, GoogleMap googleMap) {
        super(context, googleMap);
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
            if (distance < 1) {
                Toast.makeText(this.context, String.format("Minimal distance should be at least %.1f m", 1.0), Toast.LENGTH_SHORT).show();
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
     * @param surface current surface.
     */
    public void refreshView(boolean isAddingProcessFinished, Surface surface) {
        showSurfaceOnMap(surface);
        if (isAddingProcessFinished) {
            double polygonArea = surface.computeArea();
            drawPolygon(polygonArea, surface.convertToLatLngList());
//            surfaceNameButton.setVisibility(View.VISIBLE);
//            surfaceNameButton.setText(surface.getName());
        } else if (surface.getPoints().size() > 1) {
            drawPolyline(false, workingSurface);
        }
    }

    /**
     * Stop adding new points to a working surface and create the polygon on the map.
     * This will be happen by joining the last location point with the first one.
     *
     */
    public void finish() {
        refreshView(true, workingSurface);
    }


    /**
     * Stop adding new points to a working surface and reset the process.
     *
     */
    public void reset() {
        workingSurface.getPoints().clear();
        refreshView(false, workingSurface);
    }


    /**
     * Add new created surface to surfaces list.
     *
     * @param name name of the surface.
     */
    public void storeNewSurface(String name) {
        workingSurface.setName(name);
        workingSurface = new Surface(TEMP_NAME);

        refreshView(true, workingSurface);
    }

}
