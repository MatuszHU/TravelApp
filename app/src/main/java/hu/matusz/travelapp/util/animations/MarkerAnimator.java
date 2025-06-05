package hu.matusz.travelapp.util.animations;

import android.os.Handler;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.MapView;

/**
 * Utility class for animating a Marker from one location to another,
 * followed by a smooth pan to center the map on the final marker position.
 * @author mmoel
 */
public class MarkerAnimator {

    /**
     * Animates a marker from its start position to a final position over time,
     * and pans the map smoothly to center on the final location.
     *
     * @param marker   The marker to animate.
     * @param start    The starting GeoPoint (e.g. tap location).
     * @param end      The destination GeoPoint (e.g. snapped POI).
     * @param mapView  The MapView containing the marker and controller.
     */
    public static void animateMarkerTo(Marker marker, GeoPoint start, GeoPoint end, MapView mapView) {
        final long duration = 600; // animation duration in milliseconds
        final long startTime = System.currentTimeMillis();
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                float t = (System.currentTimeMillis() - startTime) / (float) duration;

                if (t >= 1.0f) {
                    // Snap to final position and pan map
                    marker.setPosition(end);
                    mapView.getController().animateTo(end);
                    mapView.invalidate();
                    return;
                }

                // Interpolate position
                double lat = start.getLatitude() + t * (end.getLatitude() - start.getLatitude());
                double lon = start.getLongitude() + t * (end.getLongitude() - start.getLongitude());

                marker.setPosition(new GeoPoint(lat, lon));
                mapView.invalidate();

                handler.postDelayed(this, 16); // ~60 fps
            }
        });
    }
}
