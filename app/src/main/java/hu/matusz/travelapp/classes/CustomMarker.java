package hu.matusz.travelapp.classes;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import hu.matusz.travelapp.MainActivity;
import hu.matusz.travelapp.R;

public class CustomMarker extends Marker {

    public CustomMarker(MapView mapView, GeoPoint p) {
        super(mapView);

        setPosition(p);
        //sets position over pin
        setAnchor(0.05f, 0.95f);
        setTitle("Dropped pin");

        setInfoWindow(new CustomMarkerInfoWindow(mapView));
        setOnMarkerClickListener((m, mv) -> {
            if (m.isInfoWindowShown()) {
                m.closeInfoWindow(); // 👈 schließt das InfoWindow
            } else {
                m.showInfoWindow(); // 👈 öffnet es
            }
            return true;
        });


        Drawable customIcon = ContextCompat.getDrawable(mapView.getContext(), R.drawable.drawing_pin);
        if (customIcon != null) {
            customIcon.setBounds(0 ,0, customIcon.getIntrinsicWidth() / 10, customIcon.getIntrinsicHeight() / 10);
            setIcon(customIcon);
        }
    }
}
