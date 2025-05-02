package hu.matusz.travelapp.databaseUtil;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import hu.matusz.travelapp.databaseUtil.dataClasses.User;
public class FirestoreDataHandler {
    FirebaseFirestore firestore;
    public void init(){
        firestore = FirebaseFirestore.getInstance();
    }
    public void voidConnection() throws ConnectException {
        firestore = null;
        Log.w("FIRESTORE","Connection dropped");
        throw new ConnectException("Database connection voided");
    }
    public FirestoreDataHandler() {
    }

    public void saveUser(String name, String id, String cc, FirebaseAuth mAuth){
        User user = new User();
        user.setUserId(id);
        user.setEmail(mAuth.getCurrentUser().getEmail());
        user.setCountryOfOriginCode(cc);
        user.setName(name);

        Map<String, String> userMap = new HashMap<>();
        userMap.put("id", user.getUserId());
        userMap.put("name", user.getName());
        userMap.put("cc", user.getCountryOfOriginCode());
        userMap.put("email", user.getEmail());

        firestore.collection("users").add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("FIRESTORE","Sikeres mentés az adatbázisba ezzel az ID-vel: "+ documentReference.getId()+"\t Firestore: "+ documentReference.getFirestore());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("FIRESTORE", "Kritikus hiba:\t", e);
            }
        });
    }
    public interface UserCallback {
        void onUserReceived(User user);
        void onError(Exception e);
    }
    public void readUser(String userId, UserCallback callback) {
        firestore.collection("users")
                .whereEqualTo("id", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                Map<String, Object> userData = document.getData();

                                User targetUser = new User();
                                targetUser.setUserId((String) userData.get("id"));
                                targetUser.setName((String) userData.get("name"));
                                targetUser.setCountryOfOriginCode((String) userData.get("cc"));
                                targetUser.setEmail((String) userData.get("email"));

                                callback.onUserReceived(targetUser);
                            } else {
                                callback.onError(new Exception("User not found"));
                            }
                        } else {
                            Log.w("FIRESTORE", "Error getting documents.", task.getException());
                            callback.onError(task.getException());
                        }
                    }
                });

    }
}
