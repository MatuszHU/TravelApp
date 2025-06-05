package hu.matusz.travelapp;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import hu.matusz.travelapp.classes.CustomMarker;
import hu.matusz.travelapp.network.NominatimService;
import hu.matusz.travelapp.util.animations.InfoPanelAnimator;
import hu.matusz.travelapp.util.UUIDGen;
import hu.matusz.travelapp.util.animations.MarkerAnimator;
import hu.matusz.travelapp.util.database.FirestoreDataHandler;
import hu.matusz.travelapp.util.database.models.GeoLocation;
import hu.matusz.travelapp.util.database.models.User;

/**
 * Activity for controlling the map
 * @author mmoel matusz
 */
public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";
    private User user;

    private MapView map;
    private LinearLayout infoPanel;
    private TextView pinTitle;
    private ImageButton closePanelButton;
    private Button deletePinButton;
    private Button savePinButton;
    private Marker selectedMarker = null;
    private FirestoreDataHandler fdt;
    private UUIDGen u;
    private int customMarkerCounter = 0;
    private IMapController mapController;


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

        //sets up FirestoreDataHandler, has no db connection for dev purpose
        Context ctx = getApplicationContext();
        fdt = new FirestoreDataHandler();
        fdt.init();

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

        mapController = map.getController();

        user = (User) getIntent().getSerializableExtra("user");

        // Initialize views
        infoPanel = findViewById(R.id.info_panel);
        pinTitle = findViewById(R.id.pin_title);
        closePanelButton = findViewById(R.id.close_panel_button);
        savePinButton = findViewById(R.id.save_pin_button);
        deletePinButton = findViewById(R.id.delete_pin_button);

        // Edit button for pin title
        ImageButton editTitleButton = findViewById(R.id.edit_pin_title_button);
        editTitleButton.setOnClickListener(v -> {
            if (selectedMarker == null) return;

            // Create input field with current title
            final EditText input = new EditText(this);
            input.setText(selectedMarker.getTitle());
            input.setSelection(input.getText().length());

            //Alert for changing the title of a pin
            new AlertDialog.Builder(this)
                    .setTitle("Edit Pin Title")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty()) {
                            selectedMarker.setTitle(newTitle);
                            pinTitle.setText(newTitle);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        // closes infoPanel
        closePanelButton.setOnClickListener(v -> {
            closeInfoPanel();
            selectedMarker = null;
        });

        // saves pin in db
        // not working rn, because of no db connection
        savePinButton.setOnClickListener(v ->{
            GeoLocation loc = new GeoLocation(selectedMarker.getPosition().getLatitude(),selectedMarker.getPosition().getLongitude(),selectedMarker.getTitle(), u.getUUID());
            fdt.saveLocation(loc);
        });

        // deletes pin
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
                addMarkerWithSnap(p); // Snap to POI
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                addMarkerAtExactPoint(p);
                return true;
            }
        }));

        // Center map on example location
        GeoPoint startPoint = new GeoPoint(39.221688, -8.687812);
        mapController.setZoom(17);
        mapController.setCenter(startPoint);

        // refresh view
        map.invalidate();
    }

    /**
     * Adds a marker by snapping to the nearest POI using reverse geocoding.
     * Falls back to the tap location if no meaningful data is found.
     *
     * @param tapPoint The original point tapped by the user.
     */
    private void addMarkerWithSnap(GeoPoint tapPoint) {

        //determines to what kind of poi a pin should snap to e.g. a building or a city
        //based on the in app zoom
        double mapZoom = map.getZoomLevelDouble();
        int nominatimZoom;

        if (mapZoom >= 14) {
            nominatimZoom = 18; // POIs/buildings
        } else if (mapZoom >= 7) {
            nominatimZoom = 10; // city/town
        } else { //zoom <= 6
            nominatimZoom = 3; // country
        }

        // tries to get the GeoPoint from Nominatim
        NominatimService.reverseGeocode(tapPoint, nominatimZoom, new NominatimService.GeocodingResultCallback() {
            /**
             * Places marker at given point
             * @param title Name of the pin
             * @param snappedPoint Location of POI where the point snaps to
             */
            @Override
            public void onResult(String title, GeoPoint snappedPoint) {
                placeMarker(snappedPoint, title, tapPoint);
            }

            /**
             * When failed to get GeoPoint fallback to tapped point
             * @param fallbackPoint Point where user tapped
             */
            @Override
            public void onError(GeoPoint fallbackPoint) {
                placeMarker(fallbackPoint, "Dropped Pin", fallbackPoint);
            }
        });
    }


    /**
     * Adds a marker directly at the long-pressed location without snapping.
     *
     * @param point The exact GeoPoint where the user long-pressed.
     */
    private void addMarkerAtExactPoint(GeoPoint point) {
        placeMarker(point, "Custom Pin " + customMarkerCounter++, point);
    }


    /**
     * Places a marker at the specified location with the given title,
     * and pans the map smoothly to center on the marker.
     *
     * @param finalPoint     The final marker location (snapped or exact).
     * @param name           The title to display for the marker.
     * @param originalPoint  The location originally tapped by the user (used for animation).
     */
    private void placeMarker(GeoPoint finalPoint, String name, GeoPoint originalPoint) {
        // Prevent duplicate markers near the same location
        if (isMarkerAlreadyPlaced(finalPoint, 5.0)) {
            Toast.makeText(MapActivity.this, "A pin already exists here", Toast.LENGTH_SHORT).show();
            return; // Skip placing if too close to an existing marker
        }

        // Create marker starting at the tap location
        CustomMarker marker = new CustomMarker(map, originalPoint);
        marker.setTitle(name);
        selectedMarker = marker;

        // Set marker click behavior to open info panel
        marker.setOnMarkerClickListener((m, mapView) -> {
            if (m.equals(selectedMarker)) return true;
            selectedMarker = m;
            openInfoPanel(m);
            mapController.animateTo(m.getPosition()); // Pan on click
            return true;
        });

        // Add marker to the map and show info panel
        map.getOverlays().add(marker);
        openInfoPanel(marker);
        map.invalidate();

        // Animate only if the snapped point is significantly different from the original
        double distanceMeters = originalPoint.distanceToAsDouble(finalPoint);
        if (distanceMeters > 0) {
            MarkerAnimator.animateMarkerTo(marker, originalPoint, finalPoint, map);
            Toast.makeText(MapActivity.this, "Snapped to nearest point of interest", Toast.LENGTH_SHORT).show();

        } else {
            marker.setPosition(finalPoint); // Set final position directly if close enough
            mapController.animateTo(finalPoint); // Pan on static pin
        }

    }

    /**
     * Checks if a marker already exists within a given radius of the specified location.
     *
     * @param location The location to check.
     * @param radiusMeters Distance threshold to consider a marker as duplicate.
     * @return true if a nearby marker exists, false otherwise.
     */
    private boolean isMarkerAlreadyPlaced(GeoPoint location, double radiusMeters) {
        for (Overlay overlay : map.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker existingMarker = (Marker) overlay;
                double distance = existingMarker.getPosition().distanceToAsDouble(location);
                if (distance < radiusMeters) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Opens the info panel to the given marker
     * @param marker The marker to which the info panel should be shown
     * @see InfoPanelAnimator
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