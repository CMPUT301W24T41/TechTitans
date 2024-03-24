package com.example.eventsigninapp;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DatabaseController {

    // cannot be final for testing purposes
    private FirebaseFirestore db;

    // cannot be final for testing purposes
    private FirebaseStorage storage;



    public DatabaseController() {
        this.db = FirebaseFirestore.getInstance();
        this.storage= FirebaseStorage.getInstance();
    }
    public DatabaseController(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.db = firestore;
        this.storage = storage;
    }


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
        userData.put("attendingEvents", user.getAttendingEvents());
        userData.put("hostingEvents", user.getHostingEvents());



        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData, SetOptions.merge());
//                .addOnSuccessListener(aVoid -> Log.d("Database", "putUserToFirestore: User data successfully updated"))
//                .addOnFailureListener(e -> Log.e("Database", "putUserToFirestore: Error updating user data", e));
    }



    /**
     * This function gets a user from the database using the given id and updates the current
     * user of a given userController object with information of that user
     * if the task fails, this creates a new user instead to add
     * @param id the id of the user to acquire
     * @param userController the UserController to update
     */
    public void updateWithUserFromFirestore(String id, UserController userController) {
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        User pulledUser = new User(
                                id,
                                document.getString("firstName"),
                                document.getString("lastName"),
                                document.getString("contact"),
                                (ArrayList<String>) document.get("attendingEvents"),
                                (ArrayList<String>) document.get("hostingEvents"));
                        userController.setUser(pulledUser);
                        this.updateWithProfPictureFromWeb(pulledUser);
                    } else {
                        // user does not exist, create a new user
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
                        User pulledUser = new User(
                                id,
                                document.getString("firstName"),
                                document.getString("lastName"),
                                document.getString("contact"),
                                (ArrayList<String>) document.get("attendingEvents"),
                                (ArrayList<String>) document.get("hostingEvents")
                        );
                        callback.onCallback(pulledUser);
                    } else {
                        callback.onError(new Exception("failed to retrieve user"));
                    }
                });
    }

    /**
     * This method deletes the given picture uri from the storage for the given user
     * @param user the user whose profile is being updated
     */
    public void deleteProfilePicture(User user) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + user.getId());

        // Delete the profile picture from Firebase Storage
        profilePicRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Upon successful deletion, update the user's picture URI
                    user.setPicture(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "deleteProfilePicture: Error, failure to delete image", e);
                });
    }

    /**
     * This method uploads the given picture uri to the storage for the given user
     * @param picture the picture to upload
     * @param user the user whose profile is being updated
     */
    public void uploadProfilePicture(Uri picture, User user) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + user.getId());

        if (picture != null) {
            // Upload file to Firebase Storage
            profilePicRef.putFile(picture)
                    .addOnSuccessListener(taskSnapshot -> {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update upon successful completion
                            user.setPicture(uri);
                        }).addOnFailureListener(e -> {
                            Log.e("Database", "uploadProfilePicture: Error, failure to get URL data", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Database", "uploadProfilePicture: Error, failure to upload image", e);
                    });
        }
    }


    /**
     * this updates the profile of a given user with the result acquired from the database
     * @param user the user to be updated
     */
    public void updateWithProfPictureFromWeb(User user) {
        StorageReference storageRef = storage.getReferenceFromUrl(user.getImgUrl());

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            user.setPicture(uri);
            this.putUserToFirestore(user); // Update the user's profile in Firestore
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
            callback.onImageUriCallback(uri);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getOtherUserProfilePicture: Failed to retrieve image", e);
            callback.onError(e);
        });
    }

    /**
     * This function retrieves users that signed up to an event from the database.
     * @param event the event we are retrieving signed up users for
     * @param callback callback function to get retrieval results
     */
    public void getSignedUpUsersFromFirestore(Event event, GetSignedUpUsersCallback callback) {
        final ArrayList<?>[] usersSignedUp = new ArrayList<?>[1]; // effectively final
        CollectionReference eventsRef = db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                usersSignedUp[0] = (ArrayList<?>) document.get("signedUpUsers");
                if (usersSignedUp[0] != null) {
                    callback.onGetSignedUpUsersCallback(event, usersSignedUp[0]);
                } else {
                    Log.e("Database", "Error retrieving signed up users");
                }
            }
        });
        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG", String.format("Error: %s", error.getMessage()));
                return;
            }
            if (value == null) {
                return;
            }

            DocumentSnapshot doc = value.getDocuments().get(0);
            usersSignedUp[0] = (ArrayList<?>) doc.get("checkedInUsers");
            if (usersSignedUp[0] != null) {
                callback.onGetSignedUpUsersCallback(event, usersSignedUp[0]);
            } else {
                Log.e("Database", "Error retrieving checked in users");
            }
        });
    }

    public void removeUserFromEvent(String userID, String eventID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.update("signedUpUsers", FieldValue.arrayRemove(userID));
    }

    /**
     * This function retrieves users that checked into an event from the database.
     * @param event the event we are retrieving checked in users for
     * @param callback callback function to get retrieval results
     */
    public void getCheckedInUsersFromFirestore(Event event, GetCheckedInUsersCallback callback) {
        final ArrayList<?>[] usersCheckedIn = new ArrayList<?>[1]; // effectively final
        CollectionReference eventsRef= db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                usersCheckedIn[0] = (ArrayList<?>) document.get("checkedInUsers");
                if (usersCheckedIn[0] != null) {
                    callback.onGetCheckedInUsersCallback(event, usersCheckedIn[0]);
                } else {
                    Log.e("Database", "Error retrieving checked in users");
                }
            }
        });
        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG", String.format("Error: %s", error.getMessage()));
                return;
            }
            if (value == null) {
                return;
            }

            DocumentSnapshot doc = value.getDocuments().get(0);
            usersCheckedIn[0] = (ArrayList<?>) doc.get("checkedInUsers");
            if (usersCheckedIn[0] != null) {
                callback.onGetCheckedInUsersCallback(event, usersCheckedIn[0]);
            } else {
                Log.e("Database", "Error retrieving checked in users");
            }
        });
    }

    public void putEventPosterToFirestore(String eventID, Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        StorageReference storageRef = storage.getReference();
        StorageReference eventPosterRef = storageRef.child("event_posters/" + eventID);

        eventPosterRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Database", "Image download URL: " + uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e("Database", "putEventPosterToFirestore: Error, failure to get URL data", e);
                }))
                .addOnFailureListener(e -> {
                    Log.e("Database", "putEventPosterToFirestore: Error, failure to upload image", e);
                });
    }

    public void putEventCheckInQRCodeToFirestore(String eventID, Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        StorageReference storageRef = storage.getReference();
        StorageReference eventQRCodeRef = storageRef.child("event_check_in_qr_codes/" + eventID);

        eventQRCodeRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> eventQRCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Database", "Image download URL: " + uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e("Database", "putEventCheckInQRCodeToFirestore: Error, failure to get URL data", e);
                }))
                .addOnFailureListener(e -> {
                    Log.e("Database", "putEventCheckInQRCodeToFirestore: Error, failure to upload image", e);
                });
    }

    public void putEventDescriptionQRCodeToFirestore(String eventID, Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        StorageReference storageRef = storage.getReference();
        StorageReference eventQRCodeRef = storageRef.child("event_description_qr_codes/" + eventID);

        eventQRCodeRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> eventQRCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Database", "Image download URL: " + uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e("Database", "putEventDescriptionQRCodeToFirestore: Error, failure to get URL data", e);
                }))
                .addOnFailureListener(e -> {
                    Log.e("Database", "putEventDescriptionQRCodeToFirestore: Error, failure to upload image", e);
                });
    }

    public void getEventImages(String eventID, EventImageUriCallbacks callbacks) {
        getEventPoster(eventID, callbacks);
        getEventCheckInQRCode(eventID, callbacks);
        getEventDescriptionQRCode(eventID, callbacks);
    }

    public void getEventPoster(String eventID, EventImageUriCallbacks callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference eventPosterRef = storageRef.child("event_posters/" + eventID);

        eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            callback.onEventPosterCallback(uri);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getEventPoster: Failed to retrieve image", e);
            callback.onError(e);
        });
    }

    public void getEventCheckInQRCode(String eventID, EventImageUriCallbacks callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference eventQRCodeRef = storageRef.child("event_check_in_qr_codes/" + eventID);

        eventQRCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            callback.onEventCheckInQRCodeCallback(uri);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getEventCheckInQRCode: Failed to retrieve image", e);
            callback.onError(e);
        });
    }

    public void getEventDescriptionQRCode(String eventID, EventImageUriCallbacks callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference eventQRCodeRef = storageRef.child("event_description_qr_codes/" + eventID);

        eventQRCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            callback.onEventDescriptionQRCodeCallback(uri);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getEventDescriptionQRCode: Failed to retrieve image", e);
            callback.onError(e);
        });
    }

    public void pushEventToFirestore(Event event) {
        String uuid = event.getUuid();
        DocumentReference eventRef = db.collection("events").document(event.getUuid());
        eventRef.set(event.toMap())
                .addOnSuccessListener(aVoid -> Log.d("Database", "pushEventToFirestore: Event data successfully updated"))
                .addOnFailureListener(e -> Log.e("Database", "pushEventToFirestore: Error updating event data", e));

        putEventCheckInQRCodeToFirestore(uuid, event.getCheckInQRCodeUri());
        putEventDescriptionQRCodeToFirestore(uuid, event.getDescriptionQRCodeUri());
        putEventPosterToFirestore(uuid, event.getPosterUri());
    }

    public void getEventFromFirestore(String uuid, GetEventCallback callback) {
        DocumentReference eventRef = db.collection("events").document(uuid);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    callback.onGetEventCallback(document.toObject(Event.class));
                } else {
                    callback.onGetEventCallback(null);
                }
            }
        });
    }

    /**
     * This function gets all the events from the database.
     * @param callback callback to add the events to the list of events
     */
    public void getAllEventsFromFirestore(GetAllEventsCallback callback) {
        CollectionReference events = db.collection("events");

        events.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            eventList.add(doc.toObject(Event.class));
                        }
                        callback.onGetAllEventsCallback(eventList);
                    } else {
                        Log.e("DEBUG", "Error retrieving events");
                    }
                });
    }

    /**
     * This method puts a check-in location to the database
     * @param event the event to put the check-in location to
     * @param location user location when they checked in
     */
    public void addCheckInLocationToFirestore(Event event, Location location) {
        GeoPoint loc = new GeoPoint(location.getLatitude(), location.getLongitude());
        DocumentReference eventsRef = db.collection("events").document(event.getUuid());
        eventsRef.update("checkInLocations", FieldValue.arrayUnion(loc))
                .addOnSuccessListener(avoid -> {
                    Log.e("DEBUG", "Successfully added check in location");
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG", "Failed to check in location");
                });
    }

    /**
     * This method gets all the check in locations for an event from the database.
     * @param event the event to get check-in locations from
     * @param callback callback function to get check-in locations
     */
    public void getCheckInLocationsFromFirestore(Event event, GetCheckInLocationCallback callback) {
        final ArrayList<?>[] checkInLocations = new ArrayList<?>[1]; // effectively final
        CollectionReference eventsRef = db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                checkInLocations[0] = (ArrayList<?>) doc.get("checkInLocations");
                if (checkInLocations[0] != null) {
                    callback.onGetCheckInLocationCallback(event, checkInLocations[0]);
                    Log.e("DEBUG", "Success retrieving check-in locations");
                } else {
                    Log.e("DEBUG", "Error retrieving check-in locations");
                }
            }
        });

        eventsRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("DEBUG", String.format("Error: %s", error.getMessage()));
                return;
            }

            if (value == null) {
                return;
            }

            DocumentSnapshot doc = value.getDocuments().get(0);
            checkInLocations[0] = (ArrayList<?>) doc.get("checkInLocations");
            if (checkInLocations[0] != null) {
                callback.onGetCheckInLocationCallback(event, checkInLocations[0]);
            } else {
                Log.e("DEBUG", "Error retrieving checked in users");
            }
        });
    }
    /** This method takes an event of type Event and a String newUserId. It then adds this user to
     * the event on firestore under the singedUpUsers array. It will not add duplicates*/
    public void addSignedUpUser(Event event, User user) {

        DocumentReference eventRef = db.collection("events").document(event.getUuid());
        eventRef.update("signedUpUsers", FieldValue.arrayUnion(user.getId()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SignUpUser", "User Signed-up to Event.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SignUpUser", "Error Signing-up to Event: " + e.getMessage());
                    }
                });
    }

    public void addEventToUser(User user, Event event){
        DocumentReference eventRef = db.collection("users").document(user.getId());
        eventRef.update("attendingEvents", FieldValue.arrayUnion(user.getId()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("AddAttendingEvents", "Attending Events updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddAttendingEvents", "Error Adding to Attending Events: " + e.getMessage());
                    }
                });


    };
    public void addFCMTokenToUser(String userID, String token){
        DocumentReference eventRef = db.collection("users").document(userID);
        eventRef.update("fcmToken", token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("AddFCMToken", "FCM Token updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddFCMToken", "Error Adding to FCM Token: " + e.getMessage());
                    }
                });
    }
    public void getEventCreatorUUID(Event event, GetEventCreatorUUIDCallback callback){
        DocumentReference eventRef = db.collection("events").document(event.getUuid());
        // Fetch the event document
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // DocumentSnapshot contains data read from a document in Firestore
                DocumentSnapshot document = task.getResult();

                // Check if the document exists and contains the creator UUID field
                if (document.exists()) {
                    String creatorUUID = document.getString("creatorUUID");

                    // Invoke the callback with the creator UUID
                    callback.onGetEventCreatorUUIDCallback(event, creatorUUID);
                } else {
                    // Document doesn't exist or creator UUID field is not found
                    callback.onGetEventCreatorUUIDCallback(event, null);
                }
            } else {
                // Error handling
                Log.e("getEventCreatorUUID", "Error getting event document", task.getException());
                callback.onGetEventCreatorUUIDCallback(event, null);
            }
        });
    }
    public interface GetEventCallback {
        void onGetEventCallback(Event event);
    }
    public interface GetEventCreatorUUIDCallback{
        void onGetEventCreatorUUIDCallback(Event event, String creatorUUID);
    }
    public interface GetCheckInLocationCallback {
        void onGetCheckInLocationCallback(Event event, ArrayList<?> checkInLocations);
    }

    public interface EventImageUriCallbacks {
        void onEventPosterCallback(Uri imageUri);
        void onEventCheckInQRCodeCallback(Uri imageUri);
        void onEventDescriptionQRCodeCallback(Uri imageUri);
        void onError(Exception e);
    }

    /**
     * This interface allows the users that signed up for an event to be retrieved from the database.
     */
    public interface GetSignedUpUsersCallback {
        void onGetSignedUpUsersCallback(Event event, ArrayList<?> users);
    }

    /**
     * This interface allows the users that checked into an event to be retrieved from the database.
     */
    public interface GetCheckedInUsersCallback {
        void onGetCheckedInUsersCallback(Event event, ArrayList<?> users);
    }

    /**
     * This interface allows an event to be retrieved from the database and added to the list of events.
     */
    public interface GetAllEventsCallback {
        void onGetAllEventsCallback(ArrayList<Event> events);
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
}