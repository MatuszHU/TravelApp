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

    public interface GeocodingCallback {
        void onSuccess(String name);
        void onError(String fallbackName);
    }

    private static final OkHttpClient client = new OkHttpClient();
    private static final String USER_AGENT = "TravelApp/1.0 (moritz-alexander.moeller@stud.hs-hannover.de)"; // ← Use valid contact!

    /**
     * Searches with the location of a point for the nearest address
     * @param point Location for which address should be found
     * @param callback Message for status of geocode
     */
    public static void reverseGeocode(GeoPoint point, GeocodingCallback callback) {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" +
                point.getLatitude() + "&lon=" + point.getLongitude() + "&zoom=18&addressdetails=1";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Nominatim", "Request failed", e);
                runOnUiThread(() -> callback.onError("Dropped Pin"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    String displayName = json.optString("display_name", "Dropped Pin");
                    runOnUiThread(() -> callback.onSuccess(displayName));
                } catch (Exception e) {
                    Log.e("Nominatim", "Parsing failed", e);
                    runOnUiThread(() -> callback.onError("Dropped Pin"));
                }
            }

            private void runOnUiThread(Runnable r) {
                new Handler(Looper.getMainLooper()).post(r);
            }
        });
    }
}
