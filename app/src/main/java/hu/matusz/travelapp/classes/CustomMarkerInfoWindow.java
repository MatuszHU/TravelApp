package hu.matusz.travelapp.classes;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import hu.matusz.travelapp.R;

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

        title.setText(marker.getTitle());

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
    }

    @Override
    public void onClose() {
        // optional:
    }
}

