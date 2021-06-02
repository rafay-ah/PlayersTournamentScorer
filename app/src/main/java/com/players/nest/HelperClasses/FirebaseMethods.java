package com.players.nest.HelperClasses;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.players.nest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseMethods {

    Context context;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    public FirebaseMethods(Context context) {
        this.context = context;
    }


    private void checkIfUsernameExists(final String username) {

        databaseReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.DB_USERS));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uName = String.valueOf(dataSnapshot.child(context.getString(R.string.dbname_userName)).getValue());
                    Log.d("mTAG", "onDataChange: " + uName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
