package hu.matusz.travelapp.databaseUtil;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dataConnect {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("HELLOWORLD");

    ref.setValue("SYSTEM_IN");

    final String TAG = "LOGDATA";
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        private void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            Log.d(TAG, "Value is: " + value);
        }

        @Override
        private void onCancelled(@NonNull DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    });

    public String inquiryResult(String DOM){
        if(DOM.startsWith("SELECT")){
            ref.setValue(DOM);
            return ref.getPrimarytInstance.toString();
        }else {
            try {
                throw new IllegalAccessException("Non Select statement");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void logger(Error e, Error e2){
        if(e.equals(e2)){
            System.err.println("Same Error produced twice! Data management error");
        }
    }
}
