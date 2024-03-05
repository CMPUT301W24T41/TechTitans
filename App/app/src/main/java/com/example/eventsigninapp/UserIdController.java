package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * This class should help control and fetch the users unique UID and acquire their information
 * from the the database
 */
public class UserIdController {

    /**
     * This interface alerts views to update when the profile information is updated
     */
    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newFirstName, String newLastName, String newContact);
    }

    private OnProfileUpdateListener onProfileUpdateListener;

    public void setOnProfileUpdateListener(OnProfileUpdateListener listener) {
        this.onProfileUpdateListener = listener;
    }

    public interface userCallback {
        void onCallback(User user);
    }

    private static User user = new User();


    private static final String deafaultUUID = "UUID_Default";
    private static final String prefName = "ID";
    private FirebaseFirestore db;

    public UserIdController() {
    }

    /**
     * getter for acquiring locally stored user from the controller,
     * a user must be loaded first or this function throws an error
     *
     * @return the current user of the controller
     */
    public User getUser() {
        return user;
    }


    /**
     * setter for setting locally stored user for the controller
     */
    public void setUser(User user) {
        this.user = user;
    }


    /**
     * Gets the user's UUID if it exists, otherwise generates a new one and saves it
     * by using a shared preference from the context provided
     *
     * @param context: the target context to pull the shared preference from
     * @return A string representing the User's UUID
     */
    public String getUserID(Context context) {
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
     *
     * @param context: the target context
     * @param id:      the new string value of the id
     */
    public void saveUUID(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences("ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(deafaultUUID, id);
        editor.apply();
    }

    /**
     * Adds a user to the database or updates an existing one based on the current user in the controller.
     */
    public void putUserToFirestore() {
        db = FirebaseFirestore.getInstance();
        // Add the new user to Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("contact", user.getContact());

        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // Success, updating profile
                })
                .addOnFailureListener(e -> {
                    // Failure
                    Log.e("Database", "addUserToFirestore: Error, new user data not added to database", e);
                });
    }



    /** Finds a user based on their unique id in the database and fetches it from the database, setting
     * the controllers current user to the new user
     *
     * @param id:       the unique id of the user to be fetched
     * @param callback: due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *                  to access a user fetched from the database, use the following code:
     *                  userIDController.getUserFromFirestore(userID, new UserIDController.userCallback() {
     *                  public void onCallback(User user) {
     *                  // code to use returned user in here
     *                  <p>
     *                  }
     *                  });
     */
    public void getUserFromFirestore(String id, userCallback callback) {
        db = FirebaseFirestore.getInstance();

        //fetch from the database where the document ID is equal to the UUID
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    // Checks if the task is successful and if the document does not exist, defaults to creating a new one
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User pulledUser = new User(id, document.getString("firstName"), document.getString("lastName"), document.getString("contact"));
                        callback.onCallback(pulledUser);
                    } else {
                        // failsafe for when the id has already been generated but does not exist in the database
                        User newUser = new User(id);
                        putUserToFirestore();
                        callback.onCallback(newUser);
                    }


                });
    }


    /**
     * @param activity ImagePicker library by Dhaval Sodha Parmar
     *                 Github: github.com/dhaval2404/imagePicker
     */
    public static void selectImage(Activity activity) {
        ImagePicker.with(activity)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }


    public static void selectImage(Fragment fragment){
        ImagePicker.with(fragment)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }

    /**
     * This method should be used to edit the profile information of the user.
     *
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param contact   the new contact information
     */
    public void editProfile(String firstName, String lastName, String contact) {
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }

        if (contact != null) {
            user.setContact(contact);
        }




        if (onProfileUpdateListener != null) {
            onProfileUpdateListener.onProfileUpdate(firstName, lastName, contact);
        }
        putUserToFirestore();

    }


    public void uploadProfilePicture(Uri picture) {
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //reference to store the image
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + user.getId());

        //upload file to Firebase Storage
        profilePicRef.putFile(picture)
                .addOnSuccessListener(taskSnapshot -> {
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    }).addOnFailureListener(e -> {
                        Log.e("Database", "addImageToStorage: Error, failure to get url data", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "addImageToStorage: Error, failure to upload image", e);
                });
    }


}

