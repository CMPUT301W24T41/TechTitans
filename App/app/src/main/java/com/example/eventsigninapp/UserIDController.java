package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * This class should help control and fetch the users unique UID and acquire their information
 * from the the database
 */
public class UserIDController {

    private static final String uuidKey = "UUID_KEY";
    private static final String prefName = "ID";
    private FirebaseFirestore db;

    public UserIDController(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Gets the user's UUID if it exists, otherwise generates a new one and saves it
     * by using a shared preference from the context provided
     * @param context: the target context to pull the shared preference from
     * @return A string representing the User's UUID
     */
    public String getUserID(Context context){
        //Gets a Shared Preference for correctly logging in the user
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String uuidString = preferences.getString(uuidKey, null);

        //generates a uuid if the user does not have a uuid
        if (uuidString == null) {
            uuidString = UUID.randomUUID().toString();
            addFirestoreEntry(uuidString);
            saveUUID(context, uuidString);
        }


        //returns the uuid of the user
        return uuidString;
    }

    /**
     * Updates the users UUID in the shared preference of the provided context
     * @param context: the target context
     * @param uuid: the new string value of the uuid
     */
    public void saveUUID(Context context, String uuid) {
        SharedPreferences preferences = context.getSharedPreferences("ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(uuidKey, uuid);
        editor.apply();
    }

    public void addFirestoreEntry(String id) {
        // Add the new user to Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", id);
        userData.put("firstName", "");
        userData.put("lastName", "");

        db.collection("users")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    // success
                })
                .addOnFailureListener(e -> {
                    // failure
                });
    }

    public Attendee getAttendeeFromFirestore(String id) {
        final Attendee[] attendee = {null};

        db.collection("users")
                .whereEqualTo(uuidKey, id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                         attendee[0] = new Attendee(document.getString(uuidKey));
                    }else{
                        // failsafe for when the id has already been generated but does not exist in the database
                        addFirestoreEntry(id);
                    }
                });

        return attendee[0];
    }
}


