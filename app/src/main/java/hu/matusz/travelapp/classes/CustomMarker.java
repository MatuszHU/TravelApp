package hu.matusz.travelapp.classes;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import hu.matusz.travelapp.R;

/**
 * Custom marker for displaying icon and setting position
 * @author mmoel
 */
public class CustomMarker extends Marker {

    /**
     * Creates Marker with icon and position
     * @param mapView view on which marker is
     * @param point location where marker should be added
     */
    public CustomMarker(MapView mapView, GeoPoint point) {
        super(mapView);
        setPosition(point);
        // sets position relative to pin
        setAnchor(0.05f, 0.95f);

        // Sets marker icon
        Drawable customIcon = ContextCompat.getDrawable(mapView.getContext(), R.drawable.drawing_pin);
        if (customIcon != null) {
            customIcon.setBounds(0 ,0, customIcon.getIntrinsicWidth() / 10, customIcon.getIntrinsicHeight() / 10);
            setIcon(customIcon);
        }
    }
}
