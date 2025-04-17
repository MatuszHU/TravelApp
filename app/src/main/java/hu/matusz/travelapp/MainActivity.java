package hu.matusz.travelapp;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.Credential;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.sql.ResultSet;

import hu.matusz.travelapp.sqlutil.SQLSelect;


public class MainActivity extends AppCompatActivity {

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


        //SQLSelect statement = new SQLSelect();
        // Note: Using backticks (`) is not necessary, but highly recommended by the developers.
        //ResultSet dataSet = statement.getData("SELECT `name` FROM `users`");
        //Log.d("SQLLog", dataSet.toString());

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(getBaseContext().getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();


        private void handleSignIn(Credential credential) {
            // Check if credential is of type Google ID
            if (credential instanceof CustomCredential customCredential
                    && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                // Create Google ID Token
                Bundle credentialData = customCredential.getData();
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

                // Sign in to Firebase with using the token
                firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
            } else {
                Log.w(TAG, "Credential is not of type Google ID!");
            }
        }

    }
}