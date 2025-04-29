package hu.matusz.travelapp;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import hu.matusz.travelapp.classes.CustomMarker;
import hu.matusz.travelapp.network.NominatimService;
import hu.matusz.travelapp.utils.InfoPanelAnimator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MapView map;
    private LinearLayout infoPanel;
    private TextView pinTitle;
    private ImageButton closePanelButton;
    private Button deletePinButton;
    private Marker selectedMarker = null;

    // only for development
    private int markerCounter = 0;

    /**
     * Creates a osm map
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();

        // Setup internal tile cache (no permissions needed)
        File osmdroidBasePath = new File(ctx.getFilesDir(), "osmdroid");
        if (!osmdroidBasePath.exists()) osmdroidBasePath.mkdirs();
        Configuration.getInstance().setOsmdroidBasePath(osmdroidBasePath);
        Configuration.getInstance().setOsmdroidTileCache(new File(osmdroidBasePath, "tiles"));

        // Load preferences using AndroidX
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx);
        Configuration.getInstance().load(ctx, prefs);

        // Set layout
        setContentView(R.layout.activity_map);

        // Initialize the MapView
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Initialize views
        infoPanel = findViewById(R.id.info_panel);
        pinTitle = findViewById(R.id.pin_title);
        closePanelButton = findViewById(R.id.close_panel_button);
        deletePinButton = findViewById(R.id.delete_pin_button);

        // closes infoPanel
        closePanelButton.setOnClickListener(v -> {
            closeInfoPanel();
            selectedMarker = null;
        });

        // configure delete button
        deletePinButton.setOnClickListener(v -> {
            if (selectedMarker != null) {

                // Alerts before deleting
                new AlertDialog.Builder(map.getContext())
                        .setTitle("Remove pin")
                        .setMessage("Do you want to remove the pin \"" + selectedMarker.getTitle() + "\" ?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            map.getOverlays().remove(selectedMarker);
                            closeInfoPanel();
                            selectedMarker = null;
                            map.invalidate();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Add event listener to detect map taps
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Add marker at tapped location
                addMarkerAt(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));

        // Center map on given location
        GeoPoint startPoint = new GeoPoint(39.235062, -8.688187); // Mate
        IMapController mapController = map.getController();
        mapController.setZoom(19);
        mapController.setCenter(startPoint);

        // Add marker
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Get doxed lol");
        map.getOverlays().add(startMarker);

        // refresh view
        map.invalidate();
    }

    /**
     * Adds a marker at a given point
     * @param point Location where marker should be added
     */
    private void addMarkerAt(GeoPoint point) {
        // Clip to nearest POI using Nominatim
        NominatimService.reverseGeocode(point, new NominatimService.GeocodingCallback() {
            @Override
            public void onSuccess(String name) {
                placeMarker(point, name);
            }

            @Override
            public void onError(String fallbackName) {
                placeMarker(point, fallbackName);
            }
        });
    }

    /**
     * Adds a marker at a given point
     * @param point Location where marker should be added
     * @param name Name of the location
     */
    private void placeMarker(GeoPoint point, String name) {
        CustomMarker marker = new CustomMarker(map, point);
        marker.setTitle(name);
        selectedMarker = marker;

        marker.setOnMarkerClickListener((m, mapView) -> {
            if (m.equals(selectedMarker)) return true;
            selectedMarker = m;
            openInfoPanel(m);
            return true;
        });

        map.getOverlays().add(marker);
        openInfoPanel(marker);
        map.invalidate();
    }


    /**
     * Opens the info panel to the given marker
     * @param marker The marker to which the info panel should be shown
     */
    private void openInfoPanel(Marker marker) {
        pinTitle.setText(marker.getTitle());
        if (!(infoPanel.getVisibility() == View.VISIBLE))
            InfoPanelAnimator.showPanel(infoPanel);
    }

    /**
     * Closes info panel
     */
    private void closeInfoPanel() {
        InfoPanelAnimator.hidePanel(infoPanel);
    }

}