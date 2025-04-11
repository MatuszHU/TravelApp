package hu.matusz.travelapp;

import org.osmdroid.config.Configuration;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();

        // Speicherpfad auf internen Speicher setzen
        File osmdroidBasePath = new File(ctx.getFilesDir(), "osmdroid");
        if (!osmdroidBasePath.exists()) {
            osmdroidBasePath.mkdirs();
        }

        Configuration.getInstance().setOsmdroidBasePath(osmdroidBasePath);
        Configuration.getInstance().setOsmdroidTileCache(new File(osmdroidBasePath, "tiles"));
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
    }

}