package hu.matusz.travelapp;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.LinearLayout;

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

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Executors;

import hu.matusz.travelapp.util.UUIDGen;
import hu.matusz.travelapp.util.animations.SimpleLoadingAnimation;
import hu.matusz.travelapp.util.database.Callback;
import hu.matusz.travelapp.util.database.FirestoreDataHandler;
import hu.matusz.travelapp.util.database.models.Comment;
import hu.matusz.travelapp.util.database.models.User;

/**
 * Activity to handle the sign in with Google Firebase
 * @author matusz
 */
public class GoogleSignInActivity extends AppCompatActivity {
    private FirestoreDataHandler fc;
    private UUIDGen u = new UUIDGen();
    public User localUser;
    private static final String GOOGLESIGNINLOGTAG = "GoogleLoginInformation";

    private FirebaseAuth mAuth;
    private CredentialManager credentialManager;
    private SimpleLoadingAnimation sla;
    private  LinearLayout containerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //out-commented for dev use without authentication
//        if(localUser!=null){
            Intent loggedInIntent = new Intent(GoogleSignInActivity.this, MapActivity.class);
           // loggedInIntent.putExtra("user", localUser);
            //sla.destroy();
            startActivity(loggedInIntent);
     //   }

        // Inicializálás MINDIG kell
        mAuth = FirebaseAuth.getInstance();
        fc = new FirestoreDataHandler();
        fc.init();
        u = new UUIDGen();
        credentialManager = CredentialManager.create(getBaseContext());
        containerLayout = findViewById(R.id.containerLayout);
        sla = new SimpleLoadingAnimation(findViewById(R.id.loadingImage));
        sla.startVariableSpeedRotation();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            fetchUserAndContinue(currentUser);
        } else {

            launchCredentialManager();
        }
    }

    private void fetchUserAndContinue(FirebaseUser user) {
        fc = new FirestoreDataHandler();
        fc.init();
        u = new UUIDGen();

        fc.getUserByEmail(user.getEmail(), new Callback<User>() {
            @Override
            public void onAnswerReceived(User result) {
                if (result != null) {
                    localUser = result;
                } else {
                    localUser = new User();
                    localUser.setUserId(u.getUUID());
                    localUser.setPhotoURI(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                    localUser.setName(user.getDisplayName());
                    localUser.setCountryOfOriginCode("");
                    localUser.setEmail(user.getEmail());
                    fc.saveUser(localUser);
                }

                updateUI(user);
            }

            @Override
            public void onError(Exception e) {
                Log.e(GOOGLESIGNINLOGTAG, "Hiba a felhasználó lekérdezésekor: " + e.getMessage());
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        sla.destroy();
    }


    //+ Testing, no longer in use
    public void firestoreTest(){
        FirestoreDataHandler fc = new FirestoreDataHandler();
        UUIDGen u = new UUIDGen();
        String uu = u.getUUID();
        fc.init();
        //fc.saveUser(new User(uu, "Teszt Elek", "teszt@teszt.hu", "HU"));
        fc.getUserById(uu, new Callback<User>() {
            @Override
            public void onAnswerReceived(User user) {
                Log.d("FIRESTORE", user.toString());
            }

            @Override
            public void onError(Exception e) {
                Log.e("FIRESTORE", "Hiba a felhasználó lekérdezésekor: " + e.getMessage());
            }
        });
        fc.saveComment(new Comment(uu, u.getUUID(), "Teszt", "This is a test comment", 6, u.getUUID()));
        fc.getAllComment(new Callback<List<Comment>>() {
            @Override
            public void onAnswerReceived(List<Comment> result) {
                for (int i = 0; i < result.size(); i++) {
                    Log.d("FIRECOMMENT", "Comment #"+(i+1)+" "+result.get(i).toString());
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("FIRECOMMENT", e.toString());
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
                        fc.getUserByEmail(user.getEmail(), new Callback<User>() {
                            @Override
                            public void onAnswerReceived(User result) {
                                if (result != null && result.getEmail() != null && !result.getEmail().isEmpty()) {
                                    localUser = result;
                                }


                                Log.d("LOGINTEST", "firebaseAuthWithGoogle: (success ág)" + localUser.toString());
                                updateUI(user);
                            }

                            @Override
                            public void onError(Exception e) {
                                localUser = new User();
                                localUser.setUserId(u.getUUID());
                                localUser.setPhotoURI(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                localUser.setName(user.getDisplayName());
                                localUser.setCountryOfOriginCode("");
                                localUser.setEmail(user.getEmail());
                                fc.saveUser(localUser);
                                Log.d("LOGINTEST", "firebaseAuthWithGoogle: (error ág)" + localUser.toString());
                            }
                        });

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
        if(localUser!=null){
            Intent loggedInIntent = new Intent(GoogleSignInActivity.this, UserActivity.class);
            loggedInIntent.putExtra("user", localUser);
            sla.destroy();
            startActivity(loggedInIntent);
        }
    }
}