package hu.matusz.travelapp.util.animations;

import android.os.Handler;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.MapView;

/**
 * Utility class for animating map markers smoothly from one GeoPoint to another.
 * @author mmoel
 */
public class MarkerAnimator {

    /**
     * Animates a marker from its starting location to a target location over time.
     *
     * @param marker   The marker to animate.
     * @param start    The starting GeoPoint (initial marker position).
     * @param end      The destination GeoPoint (target position to move to).
     * @param mapView  The MapView containing the marker, used for invalidation (redraw).
     */
    public static void animateMarkerTo(Marker marker, GeoPoint start, GeoPoint end, MapView mapView) {
        final long duration = 600; // milliseconds
        final long startTime = System.currentTimeMillis();
        final Handler handler = new Handler();

        // Run a frame-update loop using postDelayed for ~60 FPS animation
        handler.post(new Runnable() {
            @Override
            public void run() {
                float t = (System.currentTimeMillis() - startTime) / (float) duration;
                if (t >= 1.0) {
                    // End of animation — snap to final position
                    marker.setPosition(end);
                    mapView.invalidate();
                    return;
                }

                // Interpolate latitude and longitude
                double lat = start.getLatitude() + t * (end.getLatitude() - start.getLatitude());
                double lon = start.getLongitude() + t * (end.getLongitude() - start.getLongitude());

                // Update marker position and refresh the map
                marker.setPosition(new GeoPoint(lat, lon));
                mapView.invalidate();

                // Schedule next frame
                handler.postDelayed(this, 16); // ~60fps
            }
        });
    }
}
