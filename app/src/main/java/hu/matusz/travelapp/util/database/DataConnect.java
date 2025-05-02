package hu.matusz.travelapp.util.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hu.matusz.travelapp.util.database.models.Comment;
import hu.matusz.travelapp.util.database.models.GeoLocation;
import hu.matusz.travelapp.util.database.models.User;

/**
 * @deprecated Basically useless junk
 * @author Matusz
 * @version v6
 *
 */
public class DataConnect {
    private static final String TAG = "FirebaseDatabaseHelper";
    private final FirebaseDatabase database;
    private final DatabaseReference usersRef;
    private final DatabaseReference commentsRef;
    private final DatabaseReference locationsRef;

    @Deprecated
    public interface DataStatus {
        void DataIsLoaded(Object obj);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
        void DataIsError(String errorMessage);
    }
    @Deprecated
    public DataConnect() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        commentsRef = database.getReference("comments");
        locationsRef = database.getReference("locations");
    }

    @Deprecated
    public void saveUser(User user, final DataStatus dataStatus) {
        Log.d("DEBUG_CHECK", "saveUser meghívva: " + user.getUserId());
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User saved successfully");
                    if (dataStatus != null) dataStatus.DataIsInserted();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving user", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }
    @Deprecated
    public void getUser(String userId, final DataStatus dataStatus) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (dataStatus != null) dataStatus.DataIsLoaded(user);
                } else {
                    if (dataStatus != null) dataStatus.DataIsError("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "getUser:onCancelled", databaseError.toException());
                if (dataStatus != null) dataStatus.DataIsError(databaseError.getMessage());
            }
        });
    }
    @Deprecated
    public void updateUser(String userId, Map<String, Object> updates, final DataStatus dataStatus) {
        usersRef.child(userId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User updated successfully");
                    if (dataStatus != null) dataStatus.DataIsUpdated();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }
    @Deprecated
    public void deleteUser(String userId, final DataStatus dataStatus) {
        usersRef.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted successfully");
                    if (dataStatus != null) dataStatus.DataIsDeleted();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting user", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }

    @Deprecated
    public void saveLocation(GeoLocation location, final DataStatus dataStatus) {
        locationsRef.child(location.getGeoId()).setValue(location)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location saved successfully");
                    if (dataStatus != null) dataStatus.DataIsInserted();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving location", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }
    @Deprecated
    public void getLocation(String geoId, final DataStatus dataStatus) {
        locationsRef.child(geoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GeoLocation location = dataSnapshot.getValue(GeoLocation.class);
                    if (dataStatus != null) dataStatus.DataIsLoaded(location);
                } else {
                    if (dataStatus != null) dataStatus.DataIsError("Location not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "getLocation:onCancelled", databaseError.toException());
                if (dataStatus != null) dataStatus.DataIsError(databaseError.getMessage());
            }
        });
    }
    @Deprecated
    public void getLocationsNearby(double centerX, double centerY, double radius, final DataStatus dataStatus) {
        locationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GeoLocation> nearbyLocations = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    GeoLocation location = locationSnapshot.getValue(GeoLocation.class);
                    if (location != null) {
                        double distance = calculateDistance(centerX, centerY, location.getCoordX(), location.getCoordY());
                        if (distance <= radius) {
                            nearbyLocations.add(location);
                        }
                    }
                }
                if (dataStatus != null) dataStatus.DataIsLoaded(nearbyLocations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "getLocationsNearby:onCancelled", databaseError.toException());
                if (dataStatus != null) dataStatus.DataIsError(databaseError.getMessage());
            }
        });
    }

    @Deprecated
    public void saveComment(Comment comment, final DataStatus dataStatus) {
        // A Firebase automatikusan generál egy egyedi ID-t
        String commentId = comment.getCommentId();
        if (commentId == null || commentId.isEmpty()) {
            commentId = commentsRef.push().getKey();
            comment.setCommentId(commentId);
        }

        commentsRef.child(commentId).setValue(comment)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Comment saved successfully");
                    if (dataStatus != null) dataStatus.DataIsInserted();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving comment", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }
    @Deprecated
    public void getCommentsByUser(String userId, final DataStatus dataStatus) {
        Query query = commentsRef.orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
                if (dataStatus != null) dataStatus.DataIsLoaded(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "getCommentsByUser:onCancelled", databaseError.toException());
                if (dataStatus != null) dataStatus.DataIsError(databaseError.getMessage());
            }
        });
    }
    @Deprecated
    public void getCommentsByLocation(String geoId, final DataStatus dataStatus) {
        Query query = commentsRef.orderByChild("geoId").equalTo(geoId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        comments.add(comment);
                    }
                }
                if (dataStatus != null) dataStatus.DataIsLoaded(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "getCommentsByLocation:onCancelled", databaseError.toException());
                if (dataStatus != null) dataStatus.DataIsError(databaseError.getMessage());
            }
        });
    }
    @Deprecated
    public void updateComment(String commentId, Map<String, Object> updates, final DataStatus dataStatus) {
        commentsRef.child(commentId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Comment updated successfully");
                    if (dataStatus != null) dataStatus.DataIsUpdated();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating comment", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }
    @Deprecated
    public void deleteComment(String commentId, final DataStatus dataStatus) {
        commentsRef.child(commentId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Comment deleted successfully");
                    if (dataStatus != null) dataStatus.DataIsDeleted();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting comment", e);
                    if (dataStatus != null) dataStatus.DataIsError(e.getMessage());
                });
    }

    @Deprecated
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        // Euklideszi távolság számítása
        // Megjegyzés: Ha GPS koordinátákat használsz, akkor haversine képletet kellene használni
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}