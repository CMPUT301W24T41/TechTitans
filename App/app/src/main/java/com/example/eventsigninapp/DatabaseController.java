package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseController {

    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public DatabaseController() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void putUserToFirestore(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("contact", user.getContact());

        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Database", "putUserToFirestore: User data successfully updated"))
                .addOnFailureListener(e -> Log.e("Database", "putUserToFirestore: Error updating user data", e));
    }

    public void getUserFromFirestoreToUserController(String id, UserController userController) {
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User pulledUser = new User(id, document.getString("firstName"), document.getString("lastName"), document.getString("contact"));
                        userController.setUser(pulledUser);
                        userController.updateWithProfPictureFromWeb();
                    } else {
                        User createdUser = new User(id);
                        userController.setUser(createdUser);
                    }
                });
    }



    /**Finds a user based on their unique id in the database and fetches it from the database,
     * returning the user in a callback
     *
     * @param id:       the unique id of the user to be fetched
     * @param callback: due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *                  to access a user fetched from the database, use the following code:
     *                  userIDController.getOtherUserFromFirestore(userID, new UserIDController.userCallback() {
     *                  public void onCallback(User user) {
     *
     *                  }
     *                  });
     */
    public void getUserFromFirestore(String id, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User pulledUser = new User(id, document.getString("firstName"), document.getString("lastName"), document.getString("contact"));
                        callback.onCallback(pulledUser);
                    } else {
                        callback.onError(new Exception("failed to retrieve user"));
                    }
                });
    }

    // Additional methods for handling Firebase Storage operations

    public void uploadProfilePicture(Uri picture, User user, UserController userController) {
        StorageReference storageRef = storage.getReference();

        // Reference to store the image
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + user.getId());

        // Upload file to Firebase Storage
        profilePicRef.putFile(picture)
                .addOnSuccessListener(taskSnapshot -> {
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update upon successful completion
                        user.setPicture(uri);
                        userController.putUserToFirestore(); // Update the user's profile in Firestore
                    }).addOnFailureListener(e -> {
                        Log.e("Database", "uploadProfilePicture: Error, failure to get URL data", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "uploadProfilePicture: Error, failure to upload image", e);
                });
    }

    public void updateWithProfPictureFromWeb(User user, UserController userController) {
        StorageReference storageRef = storage.getReferenceFromUrl(user.getImgUrl());

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            user.setPicture(uri);
            userController.putUserToFirestore(); // Update the user's profile in Firestore
        }).addOnFailureListener(e -> {
            // Handle failure to retrieve the URL
            Log.e("Database", "updateWithProfPictureFromWeb: Failed to retrieve image URL", e);
        });
    }


    /**
     * fetches the profile picture of other users on the platform using a callback
     * @param userID the id of the user's picture to fetch
     * @param callback due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *      *                  to access a user fetched from the database, use the following code:
     *      *                  userIDController.getOtherUserProfilePicture(userID, new UserIDController.userCallback() {
     *      *                  public void onCallback(Uri picture) {
     *      *
     *      *                  }
     *      *                  });
     */
    public void getUserProfilePicture(String userID, ImageUriCallback callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + userID);

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            callback.onImageUriCallback(uri);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getOtherUserProfilePicture: Failed to retrieve image", e);
            callback.onError(e);
        });
    }


    public interface ImageUriCallback {
        void onImageUriCallback(Uri imageUri);

        void onError(Exception e);
    }

    public interface UserCallback {
        void onCallback(User user);

        void onError(Exception e);
    }
}