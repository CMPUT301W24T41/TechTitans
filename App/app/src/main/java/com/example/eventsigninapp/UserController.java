package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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


    private static final String uuidKey = "UUID_KEY";
    private static final String prefName = "ID";
    private FirebaseFirestore db;

    public UserController(){
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
        editor.putString(uuidKey, uuid);
        editor.apply();
    }

    /**
     * Adds a new user to the database with a blank first and last name
     * @param id: the unique id of the user
     * @param firstName: user's firstName
     * @param lastName: user's lastName
     */
    public void addUserToFirestore(String id, String firstName, String lastName) {
        // Add the new user to Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put(uuidKey, id);
        userData.put(firstName, "");
        userData.put(lastName, "");

        db.collection("users")
                .add(userData)
                .addOnSuccessListener(documentReference -> {
                    // success

                })
                .addOnFailureListener(e -> {
                    // failure
                    Log.e("Database", "addFirestoreEntry: Error,new user data not added to database");
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


        //fetch from the database where the document ID is equal to the UUID
        db.collection("users")
                .whereEqualTo(uuidKey, id)
                .get()
                .addOnCompleteListener(task -> {
                    // Checks if the task is successful and if the document does not exist, defaults to creating a new one
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        if(document.getString("role").equals("attendee")) {
                            Attendee attendee = new Attendee(id, document.getString("firstName"), document.getString("lastName"));
                            callback.onCallback(attendee);
                        }else{
                            Organizer organizer = new Organizer(id);
                            callback.onCallback(organizer);
                        }
                    } else {
                        // failsafe for when the id has already been generated but does not exist in the database
                        //addUserToFirestore(id, "");
                        Attendee newUser = new Attendee(id);
                        callback.onCallback(newUser);
                    }


                });
    }


}


