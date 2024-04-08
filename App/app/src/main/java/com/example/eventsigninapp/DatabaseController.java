package com.example.eventsigninapp;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import java.lang.reflect.Field;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class DatabaseController {

    // cannot be final for testing purposes
    private FirebaseFirestore db;

    // cannot be final for testing purposes
    private FirebaseStorage storage;


    public DatabaseController() {
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public DatabaseController(FirebaseFirestore firestore, FirebaseStorage storage) {
        this.db = firestore;
        this.storage = storage;
    }


    /**
     * This method stores a user or updates an existing user to the database
     *
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
        userData.put("fcmToken", user.getFcmToken());
        userData.put("homepage", user.getHomePageUrl());
        // this checks if the user is an admin
        userData.put("admin", user.isAdmin());
        userData.put("profileSet", user.isProfileSet());


        DocumentReference userDocument = db.collection("users").document(user.getId());

        userDocument.set(userData, SetOptions.merge());
//                .addOnSuccessListener(aVoid -> Log.d("Database", "putUserToFirestore: User data successfully updated"))
//                .addOnFailureListener(e -> Log.e("Database", "putUserToFirestore: Error updating user data", e));
    }

    public void putNotificationToFirestore(String title, String message, String topic, String id){
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("id", id);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("topic", topic);
        DocumentReference notificationDocument = db.collection("notifications").document(id);
        notificationDocument.set(notificationData, SetOptions.merge());
        DocumentReference announcementDocument = db.collection("announcements").document(topic);
        announcementDocument.set(notificationData, SetOptions.merge());
    }

    public void getAnnouncementFromFirestore(String topic, getAnnouncementCallback callback) {
        DocumentReference announcementRef = db.collection("announcements").document(topic);
        announcementRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, invoke the callback with the document
                    callback.onCallback(document);
                } else {
                    // Document doesn't exist
                    callback.onCallback(null); // Passing null to indicate document doesn't exist
                }
            } else {
                // Error getting document
                callback.onFailure(task.getException());
            }
        });
    }



    /**
     * This function gets a user from the database using the given id and updates the current
     * user of a given userController object with information of that user
     * if the task fails, this creates a new user instead to add
     *
     * @param id             the id of the user to acquire
     * @param userController the UserController to update
     */
    public void updateWithUserFromFirestore(String id, UserController userController) {
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                        Boolean isAdminValue = document.getBoolean("isAdmin");
////                        Log.d("admin", "is user admin?:" + isAdminValue);

                        User pulledUser = new User(
                                id,
                                document.getString("firstName"),
                                document.getString("lastName"),
                                document.getString("contact"),
                                document.getString("homepage"),
                                (ArrayList<String>) document.get("attendingEvents"),
                                (ArrayList<String>) document.get("hostingEvents"),
                                document.getBoolean("admin"),
                                document.getBoolean("profileSet")
                        );
                        userController.setUser(pulledUser);
                        this.updateWithProfPictureFromWeb(pulledUser);

                    } else {
                        // user does not exist, create a new user
                        User createdUser = new User(id);
                        userController.setUser(createdUser);
                    }
                });
    }


    /**
     * Finds a user based on their unique id in the database and fetches it from the database,
     * returning the user in a callback
     *
     * @param id:       the unique id of the user to be fetched
     * @param callback: due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *                  to access a user fetched from the database, use the following code:
     *                  userIDController.getOtherUserFromFirestore(userID, new UserIDController.userCallback() {
     *                  public void onCallback(User user) {
     *                  <p>
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
                                document.getString("homepage"),
                                (ArrayList<String>) document.get("attendingEvents"),
                                (ArrayList<String>) document.get("hostingEvents"),
                                document.getBoolean("admin"),
                                document.getBoolean("profileSet")

                        );
                        callback.onCallback(pulledUser);


                    } else {
                        callback.onError(new Exception("failed to retrieve user"));
                    }
                });
    }


    /**
     * This method deletes the given picture uri from the storage for the given event
     *
     * @param event the event picture is being deleted
     */
    public void deleteEventPicture(Event event) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("event_posters/" + event.getUuid());

        // Delete the profile picture from Firebase Storage
        profilePicRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Upon successful deletion, update the event's picture URI
                    event.setPosterUri(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "deleteEventPicture: Error, failure to delete image", e);
                });
    }



    /**
     * This method deletes the given picture uri from the storage for the given eventID
     *
     * @param eventID the eventID's picture is being deleted
     */
    public void deleteEventPicture(String eventID) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("event_posters/" + eventID);

        // Delete the profile picture from Firebase Storage
        profilePicRef.delete()
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "deleteEventPicture: Error, failure to delete image", e);
                });
    }



    /**
     * This method deletes the given picture uri from the storage for the given user
     *
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


    public void deleteImageFromUri(Uri uri) {
        StorageReference storageRef = storage.getReference();
        String path = uri.getPath();
        path = path.substring(path.indexOf("/o/") + 3);

        StorageReference profilePicRef = storageRef.child(path);
//        Log.d("deldel'", "deleteImageFromUri: " + profilePicRef);
        // Delete the profile picture from Firebase Storage
        profilePicRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Upon successful deletion, update the user's picture URI
                    Log.d("deldel", "deleteImageFromUri: Successfully deleted");
                })
                .addOnFailureListener(e -> {
                    Log.e("deldel", "deleteImageFromUri: Error, failure to delete image", e);
                });
    }

    /**
     * This method uploads the given picture uri to the storage for the given user
     *
     * @param picture the picture to upload
     * @param user    the user whose profile is being updated
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
     *
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
     *
     * @param userID   the id of the user's picture to fetch
     * @param callback due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *                 *                  to access a user fetched from the database, use the following code:
     *                 *                  userIDController.getOtherUserProfilePicture(userID, new UserIDController.userCallback() {
     *                 *                  public void onCallback(Uri picture) {
     *                 *
     *                 *                  }
     *                 *                  });
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
     * fetches the profile picture of other users on the platform using a callback,
     * this version is used to help load into the correct ImageView for list items
     *
     * @param userID   the id of the user's picture to fetch
     * @param imageView ImageView the image view upon successful acquiring image from storage
     * @param callback due to the asynchronous nature of firestore, to fetch the user properly a callback is needed
     *                 *                  to access a user fetched from the database, use the following code:
     *                 *                  userIDController.getOtherUserProfilePicture(userID, new UserIDController.userCallback() {
     *                 *                  public void onCallback(Uri picture,ImageView imageView ) {
     *                 *
     *                 *                  }
     *                 *                  });
     */

    public void getUserProfilePicture(String userID, ImageView imageView, ImageUriCallback callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + userID);

        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
            callback.onImageUriCallback(uri, imageView);
        }).addOnFailureListener(e -> {
            Log.e("Database", "getOtherUserProfilePicture: Failed to retrieve image", e);
            callback.onError(e);
        });
    }

    /**
     * Deletes the information of the given user
     *
     * @param user the user to be deleted
     */

    public void deleteUserInfo(User user) {
        db.collection("users").document(user.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful deletion
                        Log.d("Database", "User document deleted successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.e("Database", "Error deleting user document", e);
                });
    }


    /**
     * Deletes the information of the given event
     *
     * @param event the event to be deleted
     */

    public void deleteEventInfo(Event event) {
        db.collection("events").document(event.getUuid()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful deletion
                        Log.d("Database", "event document deleted successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.e("Database", "Error deleting event document", e);
                });
    }



    /**
     * Deletes the information of the given event
     *
     * @param eventID the eventID to be deleted
     */

    public void deleteEventInfo(String eventID) {
        db.collection("events").document(eventID).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle successful deletion
                        Log.d("Database", "event document deleted successfully");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.e("Database", "Error deleting event document", e);
                });
    }


    /**
     * Deletes both the users information and their user profile picture
     *
     * @param user the user to be deleted
     */

    public void deleteUser(User user) {

        // delete all hosting events of the user

        ArrayList<String> hostingEvents =  user.getHostingEvents();
        if(hostingEvents != null) {
            for (int i = 0; i < hostingEvents.size(); i++) {
                deleteEvent(hostingEvents.get(i));
            }
        }
        // delete all instances of a user attending an event

        ArrayList<String> attendingEvents = user.getAttendingEvents();

        if(attendingEvents != null) {
            for (int i = 0; i < attendingEvents.size(); i++) {
                removeUserFromEvent(user.getId(), attendingEvents.get(i));
            }
        }

        deleteUserInfo(user);
        deleteProfilePicture(user);

    }


    /**
     * Deletes both the event information and the event poster
     *
     * @param event the event to be deleted
     */
    public void deleteEvent(Event event) {
        // delete each case where a user is signed up for this event
        ArrayList<String> signedUpUser = new ArrayList<>(event.getSignedUpUsersUUIDs());
        for(int i = 0; i < signedUpUser.size(); i++){
            deleteAttendingEvent(signedUpUser.get(i), event.getUuid());
        }
        deleteHostingEvent(event.getUuid(), event.getCreatorUUID());
        // Remove event from list and notify adapter
        deleteEventInfo(event);
        deleteEventPicture(event);

        deleteImageInFolder("event_check_in_qr_codes", event.getUuid());
        deleteImageInFolder("event_description_qr_codes", event.getUuid());
    }

    /**
         * Deletes both the event information and the event poster
     *
             * @param eventID the event's ID for event to be deleted
     */
    public void deleteEvent(String eventID) {
        deleteEventInfo(eventID);
        deleteEventPicture(eventID);
    }
    /**
     * This function retrieves users that signed up to an event from the database.
     *
     * @param event    the event we are retrieving signed up users for
     * @param callback callback function to get retrieval results
     */
    public void getSignedUpUsersFromFirestore(Event event, GetSignedUpUsersCallback callback) {
        final ArrayList<?>[] usersSignedUp = new ArrayList<?>[1]; // effectively final
        CollectionReference eventsRef = db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(task -> {
            Log.e("SIGNUP", String.format("DB method called: %s", event.getUuid()));
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
    }

    public void removeUserFromEvent(String userID, String eventID) {
        DocumentReference eventRef = db.collection("events").document(eventID);
        eventRef.update("signedUpUsers", FieldValue.arrayRemove(userID));
        eventRef.update("checkedInUsers", FieldValue.arrayRemove(userID));

    }

    /**
     * This function retrieves users that checked into an event from the database.
     *
     * @param event    the event we are retrieving checked in users for
     * @param callback callback function to get retrieval results
     */
    public void getCheckedInUsersFromFirestore(Event event, GetCheckedInUsersCallback callback) {
        final ArrayList<?>[] usersCheckedIn = new ArrayList<?>[1]; // effectively final
        CollectionReference eventsRef = db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                usersCheckedIn[0] = (ArrayList<?>) document.get("checkedInUsers");
                if (usersCheckedIn[0] != null) {
                    callback.onGetCheckedInUsersCallback(event, usersCheckedIn[0]);
                } else {
                    Log.e("CHECKIN", "Error retrieving checked in users");
                }
            }
        });
    }

    public void getCheckedInUserCountFromFirestore(Event event, GetCheckedInUserCountCallback callback) {
        final HashMap<?,?>[] userCheckedInCount = new HashMap<?,?>[1]; // effectively final
        CollectionReference eventsRef = db.collection("events");
        eventsRef.document(event.getUuid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.e("db", "Called on complete");
                DocumentSnapshot document = task.getResult();
                userCheckedInCount[0] = (HashMap<?, ?>) document.get("checkedInEventCount");
                if (userCheckedInCount[0] != null) {
                    Log.e("db", "The retrieval was successful");
                    callback.onGetCheckedInUserCountCallback(event, userCheckedInCount[0]);
                } else {
                    Log.e("Database", "Error retrieving checked in users");
                }
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

    public void getEventPoster(String eventID, ImageView imageView, EventImageUriCallbacks callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference eventPosterRef = storageRef.child("event_posters/" + eventID);

        eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("Database", "Image download URL: " + uri.toString());
            callback.onEventPosterCallback(uri, imageView);

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

    public void findEventByQrResult(String qrResult, GetEventCallback callback) {
        final HashMap<?,?>[] userCheckedInCount = new HashMap<?,?>[1];
        db.collection("events")
                .whereEqualTo("eventCheckInQrCodeString", qrResult)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Getting Event manually
                        String uuid = document.getString("uuid");
                        String name = document.getString("name");
                        String creatorUUID = document.getString("creatorUUID");
                        int capacity = document.getLong("capacity").intValue();
//                        Date date =  document.getDate("date");
                        GeoPoint location = document.getGeoPoint("location");
                        String eventDetailsQrCodeString = document.getString("eventDetailsQrCodeString");
                        String eventCheckInQrCodeString = document.getString("eventCheckInQrCodeString");
                        ArrayList<String> checkedInUsers = (ArrayList) document.get("checkedInUsers");
                        ArrayList<String> signedUpUsers = (ArrayList) document.get("signedUpUsers");
                        String description = document.getString("description");


                        userCheckedInCount[0] = (HashMap<?, ?>) document.get("checkedInEventCount");
                        HashMap<String, Long> checkedInUsersCountStr = (HashMap<String, Long>) userCheckedInCount[0];

                        // Setting the event
                        Event event = new Event();
                        event.setUuid(uuid);
                        event.setName(name);
                        event.setCreatorUUID(creatorUUID);
                        event.setCapacity(capacity);
//                        event.setDate(date);
                        event.setLocation(location);
                        event.setEventDetailsQrCodeString(eventDetailsQrCodeString);
                        event.setEventCheckInQrCodeString(eventCheckInQrCodeString);
                        for (String checkedUser: checkedInUsers) {
                            event.addCheckedInUser(checkedUser);
                        }
                        for (String signedUser: signedUpUsers) {
                            event.addSignedUpUser(signedUser);
                        }
                        event.setDescription(description);
                        for (String user : checkedInUsersCountStr.keySet()) {
                            // Firebase stores numbers (checked in count) as Long, need to convert to Integer
                            Long value = checkedInUsersCountStr.get(user);
                            Integer count = (int) (long) value;
                            event.addCheckedInCount(user, count);
                        }


                        callback.onGetEventCallback(event);
                    }
                });
        db.collection("events")
                .whereEqualTo("eventDetailsQrCodeString", qrResult)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Event event = document.toObject(Event.class);
                        callback.onGetEventCallback(event);
                    }
                });
    }

    public void getAllEventsFromFirestore(GetAllEventsCallback callback) {
        CollectionReference events = db.collection("events");

        events.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String uuid = doc.getString("uuid");
                            String name = doc.getString("name");
                            String creatorUUID = doc.getString("creatorUUID");
                            int capacity = doc.getLong("capacity").intValue();
                            String description = doc.getString("description");
                            String eventDetailsQrCodeString = doc.getString("eventDetailsQrCodeString");

                            // Retrieve the two lists
                            ArrayList<String> checkedInUsers = (ArrayList<String>) doc.get("checkedInUsers");
                            ArrayList<String> signedUpUsers = (ArrayList<String>) doc.get("signedUpUsers");

                            String eventCheckInQrCodeString = doc.getString("eventCheckInQrCodeString");

                            // Create Event object using constructor
                            Event event = new Event(uuid, name, creatorUUID, capacity);
                            event.setDescription(description);
                            event.setEventDetailsQrCodeString(eventDetailsQrCodeString);
                            event.setEventCheckInQrCodeString(eventCheckInQrCodeString);
                            // Set other fields as needed
                            event.setCheckedInUsersUUIDs(checkedInUsers);
                            event.setSignedUpUsersUUIDs(signedUpUsers);
                            Log.d("displayd", "getAllEventsFromFirestore: " + event.getCheckedInUsersUUIDs());

                            // Add Event object to the list
                            eventList.add(event);
                        }
                        callback.onGetAllEventsCallback(eventList);
                    } else {
                        Log.e("DEBUG", "Error retrieving events", task.getException());
                    }
                });
    }



    public void getAllUsersFromFirestore(GetAllUsersCallback callback) {
        CollectionReference users = db.collection("users");
        users.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                User user = new User(
                                        doc.getString("id"),
                                        doc.getString("firstName"),
                                        doc.getString("lastName"),
                                        doc.getString("contact"),
                                        doc.getString("homepage"),
                                        (ArrayList<String>) doc.get("attendingEvents"),
                                        (ArrayList<String>) doc.get("hostingEvents"),
                                        doc.getBoolean("admin"),
                                        doc.getBoolean("profileSet")
                                );

                                Log.d("userCreated", "onComplete: New User created" + doc.getString("id"));
                                callback.onGetAllUserCallback(user);
                            }
                        } else {
                            Log.e("DEBUG", "Error retrieving events");
                        }
                    }
                });
    }


    public void getAllImagesFromFirestore(GetAllImagesCallback callback) {
        ArrayList<Uri> allImages = new ArrayList<>();

        // Fetch all profile pictures
        getAllImagesInFolder("event_posters", new ImageUriCallback() {
            @Override
            public void onImageUriCallback(Uri imageUri) {
                allImages.add(imageUri);
                callback.onGetAllImagesCallback(allImages);

            }

            @Override
            public void onImageUriCallback(Uri imageUri, ImageView imageView) {
                return;
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Log.d("Database", "no profile pictures", e);
            }
        });

        // Fetch all poster images
        getAllImagesInFolder("profile_pictures", new ImageUriCallback() {
            @Override
            public void onImageUriCallback(Uri imageUri) {
                allImages.add(imageUri);
                callback.onGetAllImagesCallback(allImages);

            }

            @Override
            public void onImageUriCallback(Uri imageUri, ImageView imageView) {
                return;
            }

            @Override
            public void onError(Exception e) {
                // Handle error
                Log.d("Database", "no poster images", e);
            }
        });
    }

    public void getAllImagesInFolder(String folderName, ImageUriCallback callback) {
        // Get a reference to the desired folder in Firebase Storage
        StorageReference folderRef = storage.getReference().child(folderName);

        // List all items (folders and files) within the specified folder
        folderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                // Get the download URL of the image and pass it to the callback
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    callback.onImageUriCallback(uri);
                    Log.d("succes found", "getAllImagesInFolder: foundImage" + uri
                    );
                }).addOnFailureListener(e -> {
                    // Handle any errors
                    callback.onError(e);
                });
            }

        }).addOnFailureListener(e -> {
            // Handle any errors
            callback.onError(e);
        });
    }

    /**
     * This method puts a check-in location to the database
     *
     * @param event    the event to put the check-in location to
     * @param location user location when they checked in
     */
    public void addCheckInLocationToFirestore(Event event, Location location) {
        GeoPoint loc = new GeoPoint(location.getLatitude(), location.getLongitude());
        DocumentReference eventsRef = db.collection("events").document(event.getUuid());
        eventsRef.update("checkInLocations", FieldValue.arrayUnion(loc))
                .addOnSuccessListener(avoid -> {
                    Log.e("LOCATION", "Successfully added check in location");
                })
                .addOnFailureListener(e -> {
                    Log.e("LOCATION", "Failed to check in location");
                });
    }

    /**
     * This method gets all the check in locations for an event from the database.
     *
     * @param event    the event to get check-in locations from
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
                    Log.e("CHECKIN", "Success retrieving check-in locations");
                } else {
                    Log.e("CHECKIN", "Error retrieving check-in locations");
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

    /**
     * This method takes an event of type Event and a String newUserId. It then adds this user to
     * the event on firestore under the signedUpUsers array. It will not add duplicates
     */
    public void addSignedUpUser(Event event, User user) {

        // Adding User to signedUpUsers in Event
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

        // Adding Event to attendingEvent in User
        DocumentReference userRef = db.collection("users").document(user.getId());
        userRef.update("attendingEvents", FieldValue.arrayUnion(event.getUuid()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("AddedEventToUser", "Event Added to User");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddEventToUser", "Error Adding Event to User: " + e.getMessage());
                    }
                });
    }

    public void addEventToUser(User user, Event event) {
        DocumentReference eventRef = db.collection("users").document(user.getId());
        eventRef.update("attendingEvents", FieldValue.arrayUnion(event.getUuid()))
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


    }



    public void deleteAttendingEvent(String user, String eventID) {
        DocumentReference userRef = db.collection("users").document(user);
        userRef.update("attendingEvents", FieldValue.arrayRemove(eventID));
    }

    public void deleteHostingEvent(String uuid, String creatorUUID) {
        DocumentReference userRef = db.collection("users").document(creatorUUID);
        userRef.update("hostingEvents", FieldValue.arrayRemove(uuid));

    }

    public interface GetAllImagesCallback {
        void onGetAllImagesCallback(ArrayList<Uri> allImages);
    }


    public interface GetAllUsersCallback {
        void onGetAllUserCallback(User user);
    }


    public interface GetEventCallback {
        void onGetEventCallback(Event event);
    }

    public interface GetEventCreatorUUIDCallback {
        void onGetEventCreatorUUIDCallback(Event event, String creatorUUID);
    }

    public interface GetCheckInLocationCallback {
        void onGetCheckInLocationCallback(Event event, ArrayList<?> checkInLocations);
    }


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

    /**
     *  Adds an admin promotion code to remote
     * @param adminCode code to be added
     */

    public void pushAdminCode(String adminCode){
        DocumentReference adminDoc = db.collection("admin").document(adminCode);
        Map<String, Object> data = new HashMap<>();
        data.put("code", adminCode);
        adminDoc.set(data, SetOptions.merge());

    }

    /**
     * checks if the admincode given exists on remote, if it does delete the admin code and make the user an admin
     * , otherwise does nothing
     * @param adminCode the adminCode to be checked
     * @param user the user to be upgraded if successful
     */
    public void updateAdmin(String adminCode, User user, Context context){
        DocumentReference adminDoc = db.collection("admin").document(adminCode);
        adminDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.get("code") != null) {
                    adminDoc.delete();
                    user.setAdmin(true);
                    putUserToFirestore(user);
                    Toast.makeText(context, "promoted to admin!", Toast.LENGTH_SHORT).show();
                    recreate((Activity) context);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // do nothing
            }
        });


        }


    public void deleteImageInFolder(String folderName, String imageName) {
        // Create a reference to the file to delete
        StorageReference imageRef = storage.getReference().child(folderName).child(imageName);

        // Delete the file
        imageRef.delete();
    }




    public interface getAnnouncementCallback {
        void onCallback(DocumentSnapshot document);
        void onFailure(Exception e);
    }


    public interface EventImageUriCallbacks {
        void onEventPosterCallback(Uri imageUri);

        void onEventPosterCallback(Uri imageUri, ImageView imageView);


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
     * This interface allows the count of the users that checked into an event to be retrieved from the database.
     */
    public interface GetCheckedInUserCountCallback {
        void onGetCheckedInUserCountCallback(Event event, HashMap<?,?> users);
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
        void onImageUriCallback(Uri imageUri, ImageView imageView);
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