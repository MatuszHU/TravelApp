package hu.matusz.travelapp;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.FirebaseDatabase;

import hu.matusz.travelapp.sqlutil.SQLSelect;


public class MainActivity extends AppCompatActivity {
    private SignInButton signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signInButton = findViewById(R.id.sign_in_button);

        signInButton.setSize(SignInButton.SIZE_WIDE);

        signInButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            startActivity(intent);
        });

        /*SQLSelect statement = new SQLSelect();
        Note: Using backticks (`) is not necessary, but highly recommended by the developers.
        ResultSet dataSet = statement.getData("SELECT `name` FROM `users`");
        Log.d("SQLLog", dataSet.toString());*/


        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void updateUI(@Nullable FirebaseUser user) {

    }
}