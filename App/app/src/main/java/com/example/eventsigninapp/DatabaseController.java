package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DatabaseController {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final FirebaseStorage storage = FirebaseStorage.getInstance();;

    public DatabaseController() {}

    /**
     * This method stores a user or updates an existing user to the database
     * @param user the user to add
     */
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


    /**
     * This method uploads the given picture uri to the storage for the current user in the controller class
     * @param picture the picture to upload
     */
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

    /**
     * This function retrieves users that are checked into an event
     * @param eventId id of an event
     */
    public void getCheckedInUsersFromFirestore(String eventId, AttendeeListController alController) {
         db.collection("events")
                 .document(eventId)
                 .get()
                 .addOnCompleteListener(task -> {
                     if (task.isSuccessful() && task.getResult() != null) {
                         DocumentSnapshot document = task.getResult();
                         ArrayList<?> usersCheckedIn = (ArrayList<?>) document.get("checkedInUsers");
                     }
                 });
        // DocumentReference usersRef = db.collection("events").document(eventId);
        // usersRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        //     @Override
        //     public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        //         if (error != null) {
        //             Log.e("Database", "Failed to retrieve users");
        //             return;
        //         }

        //         if (value == null) {
        //             return;
        //         }
        //         usersRef.get().addOnCompleteListener(task -> {
        //             if (task.isSuccessful() && task.getResult() != null) {
        //                 DocumentSnapshot document = task.getResult();
        //                 ArrayList<?> usersCheckedIn = (ArrayList<?>) document.get("checkedInUsers");
        //                 if (usersCheckedIn != null) {
        //                     alController.updateCheckedInUsers(usersCheckedIn);
        //                 }
        //             }
        //         });
        //     }
        // });
    }

    public void getCheckedInUsersFromFirestore(String eventId, GetCheckedInUsersCallback callback) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<?> usersCheckedIn = (ArrayList<?>) document.get("checkedInUsers");
                        if (usersCheckedIn != null) {
                            callback.onGetCheckedInUsersCallback(document.toObject(Event.class), usersCheckedIn);
                        }
                    }
                });
    }

    public void getSignedUpUsersFromFirestore(String eventId, GetSignedUpUsersCallback callback) {
        db.collection("events")
                .document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<?> usersSignedUp = (ArrayList<?>) document.get("signedUpUsers");
                        if (usersSignedUp != null) {
                            callback.onGetSignedUpUsersCallback(document.toObject(Event.class), usersSignedUp);
                        }
                    }
                });
    }

    public void pushEventToFirestore(Event event) {
        DocumentReference eventRef = db.collection("events").document(event.getUuid());
        eventRef.set(event.toMap())
                .addOnSuccessListener(aVoid -> Log.d("Database", "pushEventToFirestore: Event data successfully updated"))
                .addOnFailureListener(e -> Log.e("Database", "pushEventToFirestore: Error updating event data", e));
    }

    public void getEventFromFirestore(String uuid, GetEventCallback callback) {
        DocumentReference eventRef = db.collection("events").document(uuid);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    callback.onGetEventCallback(document.toObject(Event.class), uuid);
                } else {
                    callback.onGetEventCallback(null, null);
                }
            }
        });
    }

    public interface GetEventCallback {
        void onGetEventCallback(Event event, String uuid);
    }

    /**
     * This interface allows images to be retrieved
     */
    public interface ImageUriCallback {
        void onImageUriCallback(Uri imageUri);
        void onError(Exception e);
    }

    /**
     * This interface allows users to be retrieved
     */
    public interface UserCallback {
        void onCallback(User user);

        void onError(Exception e);
    }

    /**
     * This interface allows signed up users to be retrieved
     */
    public interface GetSignedUpUsersCallback {
        void onGetSignedUpUsersCallback(Event event, List<?> userIDs);
    }

    public interface GetCheckedInUsersCallback {
        void onGetCheckedInUsersCallback(Event event, List<?> userIDs);
    }
}