package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * This class should help control and fetch the users unique UID and acquire their information
 * from the the database
 */
public class UserController {

    public interface userCallback {
        void onCallback(User user);
    }


    private static final String deafaultUUID = "UUID_Default";
    private static final String prefName = "ID";
    private FirebaseFirestore db;

    public UserController(){

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
        String uuidString = preferences.getString(deafaultUUID, null);

        //generates a uuid if the user does not have a uuid
        if (uuidString == null) {
            uuidString = UUID.randomUUID().toString();
            //addUserToFirestore(uuidString, "attendee");
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
        editor.putString(deafaultUUID, uuid);
        editor.apply();
    }

    /**
     * Adds a new user to the database or updates an existing one.
     * @param user: the user object to add or modify to the database
     */
    public void putUserToFirestore(User user) {
        db = FirebaseFirestore.getInstance();
        // Add the new user to Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("contact", user.getContact());

        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Success
                })
                .addOnFailureListener(e -> {
                    // Failure
                    Log.e("Database", "addUserToFirestore: Error, new user data not added to database", e);
                });
    }


    /** Finds a user based on their unique id in the database and fetches it from the database
     * @param id: the unique id of the user to be fetched
     * @param callback: due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *  to access a user fetched from the database, use the following code:
     *      userIDController.getUserFromFirestore(userID, new UserIDController.userCallback() {
     *                   public void onCallback(User user) {
     *                       // code to use returned user in here
     *
     *                   }
     *               });
     */
    public void getUserFromFirestore(String id, userCallback callback) {
        db = FirebaseFirestore.getInstance();

        //fetch from the database where the document ID is equal to the UUID
        db.collection("users")
                .whereEqualTo(deafaultUUID, id)
                .get()
                .addOnCompleteListener(task -> {
                    // Checks if the task is successful and if the document does not exist, defaults to creating a new one
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User user = new User(id, document.getString("firstName"), document.getString("lastName"), document.getString("contact"));
                        callback.onCallback(user);
                    } else {
                        // failsafe for when the id has already been generated but does not exist in the database
                        //addUserToFirestore(id, "");
                        User emptyUser = new User(id);
                        callback.onCallback(emptyUser);
                    }


                });
    }



}


