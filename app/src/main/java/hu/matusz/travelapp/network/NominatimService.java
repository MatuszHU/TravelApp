package hu.matusz.travelapp.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class to use nominatim service for geocoding
 */
public class NominatimService {

    private static final OkHttpClient client = new OkHttpClient();
    private static final String USER_AGENT = "TravelApp/1.0 (moritz-alexander.moeller@stud.hs-hannover.de)"; // ← Use valid contact!

    public interface GeocodingResultCallback {
        void onResult(String displayName, GeoPoint snappedLocation);
        void onError(GeoPoint originalPoint);
    }

    /**
     * Searches with the location of a point for the nearest address
     * @param point Location for which address should be found
     * @param callback Message for status of geocode
     */
    public static void reverseGeocode(GeoPoint point, GeocodingResultCallback callback) {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                point.getLatitude() + "&lon=" + point.getLongitude() + "&zoom=18&addressdetails=1";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            /**
             * Sends a callback, when request to Nominatim failes
             * @param call Call
             * @param e Exception
             */
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError(point));
            }


            /**
             * When request successful provides Geopoint
             * @param call Call
             * @param response The geo response of Nominatim
             * @throws IOException Exception
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);


                    String name = json.optString("name", "Dropped Pin");
                    double lat = json.optDouble("lat", point.getLatitude());
                    double lon = json.optDouble("lon", point.getLongitude());
                    GeoPoint snappedPoint = new GeoPoint(lat, lon);

                    runOnUiThread(() -> callback.onResult(name, snappedPoint));
                } catch (Exception e) {
                    runOnUiThread(() -> callback.onError(point));
                }
            }

            /**
             * Looper for geocoding
             * @param r Runnable
             */
            private void runOnUiThread(Runnable r) {
                new Handler(Looper.getMainLooper()).post(r);
            }
        });
    }
}
