package com.example.locationmarker.markers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.locationmarker.R;
import com.example.locationmarker.surface.LocationPoint;
import com.example.locationmarker.surface.SurfaceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.maps.android.SphericalUtil.interpolate;

public class MarkersContainer implements GoogleMap.OnMarkerClickListener, Comparator<LatLng> {
    private static final String TAG = MarkersContainer.class.getSimpleName();
    // vars
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static MarkersContainer instance;
    private static GoogleMap map;
    private ArrayList<MyMarker> mMarkersList;
    private Polyline polyline;

    public static void setMap(GoogleMap map) {
        MarkersContainer.map = map;
        map.setOnMarkerClickListener(instance);
    }

    public static void setContext(Context context) {
        MarkersContainer.context = context;
    }

    public static MarkersContainer getInstance() {
        if (instance == null) {
            return new MarkersContainer();
        }
        return instance;
    }

    public MarkersContainer() {
        instance = this;
        this.mMarkersList = new ArrayList<>();
    }

    public void addMarker(Location location) {
        if (isEnoughFarDistanceBetweenOtherMarkers(location)) {
            mMarkersList.add(new MyMarker(location));
        }

        map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Test location " + mMarkersList.size())
        )/*.setIcon(icon)*/;
    }

    boolean isEnoughFarDistanceBetweenOtherMarkers(Location location) {
        for (MyMarker myMarker : mMarkersList) {
            Location markerLocation = myMarker.getLocation();
            double distance = distance(markerLocation.getLatitude(), location.getLatitude(),
                    markerLocation.getLongitude(), location.getLongitude(),
                    0.0, 0.0);
            if (distance < 0.5) {
                return false;
            }
        }
        return true;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);


        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density * 5;

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(110, 110, 110));
        // text size in pixels
        paint.setTextSize((int) (12) * scale);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
        // draw text to the Canvas center

        Rect bounds = new Rect();
        String mText = "!!!";
        paint.getTextBounds(mText, 0, mText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 6;
        int y = (bitmap.getHeight() + bounds.height()) / 5;

        canvas.drawText("!!!", x * scale, y * scale, paint);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2) + Math.pow(el1 - el2, 2);
        return Math.sqrt(distance);
    }

    public int compare(LatLng o1, LatLng o2) {
        return (int) (o2.latitude - o1.latitude) * 10 * 1000;
    }

    public void drawPolyline(boolean isAddingProcessFinished) {
        if (polyline != null) {
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions().color(Color.GREEN);
        List<LatLng> points = getLatLngFromLocation();
        polyline = map.addPolyline(polylineOptions.addAll(points));
        writeDistancesOnMap(isAddingProcessFinished);
    }

    private List<LatLng> getLatLngFromLocation() {
        List<LatLng> points = new ArrayList<>();
        for (LocationPoint locationPoint : SurfaceManager.getInstance().getCurrentSurface().getLocationPoints()) {
            Location location = locationPoint.getLocation();
            points.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        return points;
    }

    private void writeDistancesOnMap(boolean isAddingProcessFinished) {
        List<LatLng> points = getLatLngFromLocation();
        int len = points.size();
        LatLng locStart, locEnd;
        for (int i = 0; i < len; i++) {
            locStart = points.get(i);
            if (isAddingProcessFinished && i == len - 1) {
                // if process is finished and the point is the last one, connect first and last point with polyline
                locEnd = points.get(0);
            } else if (i == len - 1) {
                continue;
            } else {
                locEnd = points.get(i + 1);
            }

            double distance = distance(locStart.latitude, locEnd.latitude,
                    locStart.longitude, locEnd.longitude, 0, 0);

            LatLng middle = interpolate(locStart, locEnd, 0.5);

            IconGenerator icg = new IconGenerator(context);
            icg.setColor(Color.GREEN); // transparent background
            icg.setTextAppearance(R.style.BlackText); // black text
            Bitmap bm = icg.makeIcon(String.format("%.2f", distance) + "m");

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(middle.latitude, middle.longitude))
                    .title(new DecimalFormat("#.00").format(distance))
                    .icon(BitmapDescriptorFactory.fromBitmap(bm));

            map.addMarker(markerOptions);
        }
    }

    public double computeArea() {
        if (mMarkersList.size() <= 2) {
            return 0;
        }
        List<LatLng> points = new ArrayList<>();
        for (MyMarker myMarker : mMarkersList) {
            points.add(new LatLng(myMarker.getLocation().getLatitude(), myMarker.getLocation().getLongitude()));
        }
        double surface = SphericalUtil.computeArea(points);
        Log.d(TAG, "computeArea " + surface);
        PolygonOptions polygonOptions = new PolygonOptions().addAll(points).strokeWidth(0);
        map.addPolygon(polygonOptions);
        return surface;
    }

    public void clearMarkersList() {
        map.clear();
        mMarkersList.clear();
    }

    @Override
    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
        Toast.makeText(context, "Clicked " + marker.getId(), Toast.LENGTH_SHORT).show();
        return false;
    }
}
