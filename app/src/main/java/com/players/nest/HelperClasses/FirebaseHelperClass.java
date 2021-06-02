package com.players.nest.HelperClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import static com.players.nest.HelperClasses.Constants.USER_PROFILE_PIC;

public class FirebaseHelperClass {

    static FirebaseUser firebaseUser;
    static DatabaseReference databaseReference;


    public static void changeStatus(String status) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid());

            if (status.equals(Constants.ONLINE)) {
                HashMap<String, Object> online = new HashMap<>();
                online.put("status", Constants.ONLINE);
                online.put("lastActiveTime", 0);

                databaseReference.updateChildren(online);
            } else {
                HashMap<String, Object> offline = new HashMap<>();
                offline.put("status", Constants.OFFLINE);

                //Set Current time as Last active Time
                offline.put("lastActiveTime", System.currentTimeMillis());
                databaseReference.updateChildren(offline);
            }
        }
    }


    public static void getDeviceToken() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {

            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    databaseReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(firebaseUser.getUid());

                    HashMap<String, Object> token = new HashMap<>();
                    token.put("deviceToken", task.getResult());

                    databaseReference.updateChildren(token);
                }
            });
        }
    }

    public static void profilePicIsNull() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(USER_PROFILE_PIC, "");
            databaseReference.updateChildren(hashMap);
        }

    }
}
