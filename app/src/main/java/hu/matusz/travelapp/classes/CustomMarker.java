package hu.matusz.travelapp.classes;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import hu.matusz.travelapp.MainActivity;
import hu.matusz.travelapp.R;

/**
 * Custom marker
 */
public class CustomMarker extends Marker {

    /**
     * Constructor
     * @param mapView view on which marker is
     * @param point location where marker should be added
     */
    public CustomMarker(MapView mapView, GeoPoint point) {
        super(mapView);
        setPosition(point);

        // Sets marker icon
        Drawable customIcon = ContextCompat.getDrawable(mapView.getContext(), R.drawable.drawing_pin);
        if (customIcon != null) {
            customIcon.setBounds(0 ,0, customIcon.getIntrinsicWidth() / 10, customIcon.getIntrinsicHeight() / 10);
            setIcon(customIcon);
        }
    }
}
