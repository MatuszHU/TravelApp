package hu.matusz.travelapp;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.TestOnly;

import java.util.concurrent.Executors;

import hu.matusz.travelapp.util.database.FirestoreDataHandler;
import hu.matusz.travelapp.util.database.models.User;


public class GoogleSignInActivity extends AppCompatActivity {

    private static final String GOOGLESIGNINLOGTAG = "GoogleLoginInformation";

    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;

    private  LinearLayout containerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.login_activity);
        credentialManager = CredentialManager.create(getBaseContext());
        containerLayout = findViewById(R.id.containerLayout);
        launchCredentialManager();


        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            firestoreTest();
        });
    }

    //* Testing
    public void firestoreTest(){
        FirestoreDataHandler fc = new FirestoreDataHandler();
        fc.init();
        fc.saveUser(new User("uAAA", "Teszt Elek", "teszt@teszt.hu", "HU"));
        fc.readUser("u001", new FirestoreDataHandler.UserCallback() {
            @Override
            public void onAnswerReceived(User user) {
                Log.d("FIRESTORE", user.toString());
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIRESTORE", "Hiba a felhasználó lekérdezésekor: " + e.getMessage());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void launchCredentialManager() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();
        credentialManager.getCredentialAsync(
                getBaseContext(),
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result.getCredential());
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        Log.e(GOOGLESIGNINLOGTAG, "Couldn't retrieve user's credentials: " + e.getLocalizedMessage());
                        Log.e(GOOGLESIGNINLOGTAG, "Error Type: " + e.getClass().getSimpleName());
                        Log.e(GOOGLESIGNINLOGTAG, "Localized Message: " + e.getLocalizedMessage());
                        Log.e(GOOGLESIGNINLOGTAG, "Full Exception: ", e);
                    }
                }
        );
    }


    private void handleSignIn(Credential credential) {

        if (credential instanceof CustomCredential customCredential
                && credential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            Bundle credentialData = customCredential.getData();
            GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Log.w(GOOGLESIGNINLOGTAG, "Credential is not of type Google ID!");
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Log.d(GOOGLESIGNINLOGTAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(GOOGLESIGNINLOGTAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }
    private void signOut() {
        mAuth.signOut();

        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        updateUI(null);
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(GOOGLESIGNINLOGTAG, "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        //? TEST TEXT
        TextView newTextView = new TextView(this);
        newTextView.setText("Új elem");
        newTextView.setTextSize(18);
        newTextView.setPadding(0, 10, 0, 10);

        containerLayout.addView(newTextView);

    }
}