package hu.matusz.travelapp.classes;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import hu.matusz.travelapp.R;

/**
 * Custom InfoWindow
 */
public class CustomMarkerInfoWindow extends InfoWindow {

    public CustomMarkerInfoWindow(MapView mapView) {
        super(R.layout.marker_info_window, mapView);
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;

        View view = mView;
        TextView title = view.findViewById(R.id.title);
        Button removeButton = view.findViewById(R.id.remove_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);

        title.setText(marker.getTitle());

        //Removing pin via button
        removeButton.setOnClickListener(v -> {
            new AlertDialog.Builder(mMapView.getContext())
                    .setTitle("Remove pin")
                    .setMessage("Do you want to remove the pin \"" + marker.getTitle() + "\" ?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        mMapView.getOverlays().remove(marker);
                        mMapView.invalidate();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            close(); // close InfoWindow
        });

        //Close info window through Button
        closeButton.setOnClickListener(v -> {
            marker.closeInfoWindow(); //
        });
    }

    @Override
    public void onClose() {
        // optional:
    }
}

