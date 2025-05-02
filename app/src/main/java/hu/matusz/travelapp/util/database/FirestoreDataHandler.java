package hu.matusz.travelapp.util.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import hu.matusz.travelapp.util.database.models.Comment;
import hu.matusz.travelapp.util.database.models.GeoLocation;
import hu.matusz.travelapp.util.database.models.User;
public class FirestoreDataHandler{

    public interface UserCallback{
        void onAnswerReceived(User user);
        void onError(Exception e);
    }
    public interface CommentCallback{
        void onAnswerReceived(Comment comment);
        void onError(Exception e);
    }
    public interface LocationCallback{
        void onAnswerReceived(GeoLocation geo);
        void onError(Exception e);
    }

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

    /**
     * Saves user to <i style="color: red">Firestore</i>
     * @author Matusz
     * @version v1
     * @param user a non-null <b>User</b>, only defined after log-in process.
     * @see User
     */
    public void saveUser(@NonNull User user){
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
    /**
     * Search and read a specific comment from <i style="color: red">Firestore</i>.
     * @author Matusz
     * @version v1
     * @param userId Primary identifer for <i>users</i>
     * @param callback Interface separeting the cloud function from the main thread.
     * @see User
     */

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

                                callback.onAnswerReceived(targetUser);
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
    /**
     * Saves comment to <i style="color: red">Firestore</i>
     * @author Matusz
     * @version v1
     * @param comment a non-null <b>Comment</b>.
     * @see Comment
     */
    public void saveComment(@NonNull Comment comment){
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("userId",comment.getUserId());
        commentMap.put("commentId", comment.getCommentId());
        commentMap.put("geoId", comment.getGeoId());
        commentMap.put("comment", comment.getComment());
        commentMap.put("rate", comment.getRate()+"");
        commentMap.put("title", comment.getTitle());

        firestore.collection("comments").add(commentMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    /**
     * Search and read a specific comment from <i style="color: red">Firestore</i>.
     * @author Matusz
     * @version v1
     * @param commentId Primary identifer for <i>comment</i>
     * @param callback Interface separeting the cloud function from the main thread.
     * @see Comment
     */
    public void readComment(String commentId, CommentCallback callback) {
        firestore.collection("comments")
                .whereEqualTo("id", commentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                Map<String, Object> commentData = document.getData();

                                Comment targetComment = new Comment();
                                targetComment.setUserId((String) commentData.get("userId"));
                                targetComment.setCommentId((String) commentData.get("commentId"));
                                targetComment.setComment((String) commentData.get("comment"));
                                targetComment.setRate((Integer) commentData.get("rate"));
                                targetComment.setTitle((String) commentData.get("title"));
                                targetComment.setGeoId((String) commentData.get("geoId"));

                                callback.onAnswerReceived(targetComment);
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
    public void readAllComment(){

    }
}
