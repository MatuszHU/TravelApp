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
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;

import hu.matusz.travelapp.classes.CustomMarkerInfoWindow;

public class MainActivity extends AppCompatActivity {
    private MapView map;

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
        setContentView(R.layout.activity_main);

        // Initialize the MapView
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Add event listener to detect map taps
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                // Add marker at tapped location
                Marker marker = new Marker(map);
                marker.setPosition(p);
                marker.setAnchor(0.05f, 0.95f);
                marker.setTitle("Dropped Pin");

                marker.setInfoWindow(new CustomMarkerInfoWindow(map));
                marker.setOnMarkerClickListener((m, mapView) -> {
                    if (m.isInfoWindowShown()) {
                        m.closeInfoWindow(); // 👈 schließt das InfoWindow
                    } else {
                        m.showInfoWindow(); // 👈 öffnet es
                    }
                    return true; // wir haben den Klick verarbeitet
                });


                // Optional: use your custom icon
                Drawable customIcon = ContextCompat.getDrawable(MainActivity.this, R.drawable.drawing_pin);
                if (customIcon != null) {
                    //setting size not working
                    customIcon.setBounds(0 ,0, customIcon.getIntrinsicWidth() / 10, customIcon.getIntrinsicHeight() / 10);
                    marker.setIcon(customIcon);
                }

                map.getOverlays().add(marker);
                map.invalidate(); // Refresh map

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

        // Optional: refresh view
        map.invalidate();
    }


}