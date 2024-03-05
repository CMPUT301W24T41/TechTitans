package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
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



/**
 * This class should help control and fetch the users unique UID and acquire
 * their information from the the database
 */
public class UserController {


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
    public interface userCallback {
        void onCallback(User user);

        void onError(Exception e);
    }

    // this represents the current user
    private static User user = new User();


    //this represents the key to the default id of the user
    private static final String deafaultUUID = "UUID_Default";

    //this represents the name to the preference that stores the id
    private static final String prefName = "ID";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    /**
     * creates a new UserController object and gets an instance of firestore and storage
     */
    public UserController(){
        db = FirebaseFirestore.getInstance();
        storage =  FirebaseStorage.getInstance();
    }

    //this constructor is made for testing only
    public UserController(FirebaseFirestore db, FirebaseStorage storage){
        this.db = db;
        this.storage = storage;
    }

    /**
     * getter for acquiring locally stored user from the controller,
     * a user must be loaded first or this function throws an error
     * @return the current user of the controller
     */
    public User getUser() {
        return user;
    }


    /**
     * setter for setting locally stored user for the controller
     */
    public void setUser(User user) {
        UserController.user = user;
    }


    /**
     * Gets the user's UUID if it exists, otherwise generates a new one and saves it
     * by using a shared preference from the context provided
     *
     * @param context: the target context to pull the shared preference from, this is intended to be used in MainActivity
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
     * Updates the users UUID in the shared preference of the provided context,
     * saving it for the next time the app is opened
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
     * Adds current user to the database or updates an existing one based on the current user in the controller.
     */
    public void putUserToFirestore() {
        //add the new user to Firestore
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("contact", user.getContact());

        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    //Success, updating profile
                    Log.d("Database", "addUserToFirestore: user data successfully updated");

                })
                .addOnFailureListener(e -> {
                    // Failure
                    Log.e("Database", "addUserToFirestore: Error, new user data not added to database", e);
                });
    }


    /**
     * This function gets a user from the database using the given id and updates the current user of this class to the acquired user from the database,
     * if the task fails, this creates a new user insteat
     * @param id the id of the user to acquire
     */
    public void getUserFromFirestore(String id) {
        //fetch from the database where the document ID is equal to the UUID
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    // Checks if the task is successful and if the document does not exist, defaults to creating a new one
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User pulledUser = new User(id, document.getString("firstName"), document.getString("lastName"), document.getString("contact"));
                        this.setUser(pulledUser);
                        updateWithProfPictureFromWeb();
                    } else {
                        // user does not exist
                        User createdUser = new User(id);
                        this.setUser(createdUser);
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
    public void getOtherUserFromFirestore(String id, userCallback callback) {
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
                        // user does not exist
                        callback.onError(new Exception("failed to retrieve user"));
                    }


                });
    }


    /**
     * This creates a instance of imagepicker when called in the given activity
     * @param activity the activity that calls the imagepicker
     *
     * ImagePicker library by Dhaval Sodha Parmar
     * Github: github.com/dhaval2404/imagePicker
     */
    public static void selectImage(Activity activity) {
        ImagePicker.with(activity)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }


    /**
     * This creates a instance of imagepicker when called in the given fragment
     * @param fragment the fragment that calls the imagepicker
     *
     * ImagePicker library by Dhaval Sodha Parmar
     * Github: github.com/dhaval2404/imagePicker
     */
    public static void selectImage(Fragment fragment){
        ImagePicker.with(fragment)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }

    /**
     * This method edits the parameters of each of the users profile information
     * and updates the database with the new info
     *
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param contact   the new contact information
     * @param pictureUri the new URI of the profile picture
     */
    public void editProfile(String firstName, String lastName, String contact, Uri pictureUri) {
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }

        if (contact != null) {
            user.setContact(contact);
        }

        if (pictureUri != null) {
            user.setPicture(pictureUri);
        }

        putUserToFirestore();

    }


    /**
     * This method uploads the given picture uri to the storage for the current user in the controller class
     * @param picture the picture to upload
     */
    public void uploadProfilePicture(Uri picture) {
        StorageReference storageRef = storage.getReference();

        //reference to store the image
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + user.getId());

        //upload file to Firebase Storage
        profilePicRef.putFile(picture)
                .addOnSuccessListener(taskSnapshot -> {
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        //update upon successfully completion
                        this.updateWithProfPictureFromWeb();

                    }).addOnFailureListener(e -> {
                        Log.e("Database", "addImageToStorage: Error, failure to get url data", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "addImageToStorage: Error, failure to upload image", e);
                });
    }


    /**
     * This updates/fetches the current users profile picture stored in the storage online
     */
    public void updateWithProfPictureFromWeb() {
        StorageReference storageRef = storage.getReferenceFromUrl(user.getImgUrl());

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("database", "Image download URL: " + uri.toString());
                user.setPicture(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve the URL
                Log.e("database", "failedImageRetrieval: ");
            }
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

    public void getOtherUserProfilePicture(String userID, ImageUriCallback callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + userID);

        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("database", "Image download URL: " + uri.toString());
                callback.onImageUriCallback(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("database", "failedImageRetrieval: failed to get image");
                callback.onError(e);
            }
        });

    }
}

