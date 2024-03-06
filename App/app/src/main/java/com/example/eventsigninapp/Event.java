package com.example.eventsigninapp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Event {
    private final String uuid;
    private String name;

    // The capacity of the event, 0 if uncapped
    private int capacity;
    private final Collection<User> signedUpUsers; // collection of signed up users
    private final Collection<User> checkedInUsers; // collection of checked in users
    private Object eventPoster;
    private Object qrCode;
    private Object location;
    private Date date;

    public Event() {
        //TODO: generate a unique id on creation
        uuid = UUID.randomUUID().toString();
        name = "";
        checkedInUsers = new ArrayList<User>();
        signedUpUsers = new ArrayList<User>();
        eventPoster = null;
        qrCode = null;
        location = null;
        date = null;
        capacity = 10000000;
    }

    /**
     * This method should be used to get the id of the event
     * @return the id of the event
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * This method should be used to get the name of the event
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * This method should be used to set the name of the event
     * @param name the name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method should be used to set the location of the event
     * @param location the name of the event
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This method should be used to get the capacity of the event
     * @return the capacity of the event
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * This method should be used to set the capacity of the event
     * @param capacity the capacity of the event to set
     */
    public void setCapacity(int capacity) throws IllegalArgumentException {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

        this.capacity = capacity;
    }

    /**
     * This method should be used to check if the event is capped
     * @return a boolean indicating if the event is capped
     */
    public boolean isCapped() {
        return capacity > 0;
    }

    /**
     * This method should be used to check if the event is capped and full
     * @return a boolean indicating if the event is capped and full
     */
    public boolean isFull() {
        return isCapped() && signedUpUsers.size() >= capacity;
    }

    /**
     * This method should be used to check in a user for an event
     * @param user the user to check in
     * @throws AlreadyCheckedInException if the user is already checked in to the event
     */
    public void checkInUser(User user) throws AlreadyCheckedInException {
        if (checkedInUsers.contains(user)) {
            throw new AlreadyCheckedInException("Attendee is already checked in to the event");
        }

        checkedInUsers.add(user);
    }

    /**
     * This method should be used to get the checked in users for an event
     *
     * @return the checked in users for the event
     */
    public Collection<User> getCheckedInUsers() {
        return checkedInUsers;
    }

    /**
     * This method should be used to check if a user is checked in for an event
     * @return a boolean indicating if the user is checked in for the event
     */
    public boolean isUserCheckedIn(User user) {
        return checkedInUsers.contains(user);
    }

    /**
     * This method should be used to sign up a user for an event
     * @param user the user to sign up
     * @throws EventFullException if the event is full
     * @throws AlreadySignedUpException if the user is already signed up for the event
     */
    public void signUpUser(User user) throws EventFullException, AlreadySignedUpException {
        if (isCapped() && signedUpUsers.size() >= capacity) {
            throw new EventFullException("Event is full");
        }

        if (isUserSignedUp(user)) {
            throw new AlreadySignedUpException("Attendee is already signed up for the event");
        }

        signedUpUsers.add(user);
    }

    /**
     * This method should be used to get the signed up users for an event
     * @return the signed up users for the event
     */
    public Collection<User> getSignedUpUsers() {
        return signedUpUsers;
    }

    /**
     * This method should be used to check if a user is signed up for an event
     * @return the signed up users for the event
     */
    public boolean isUserSignedUp(User user) {
        return signedUpUsers.contains(user);
    }

    /**
     * This class should be thrown when a user tries to sign up for an event that is full
     */
    public static class EventFullException extends Exception {
        public EventFullException(String message) {
            super(message);
        }
    }

    /**
     * This class should be thrown when a user tries to sign up for an event that they are already signed up for
     */
    public static class AlreadySignedUpException extends Exception {
        public AlreadySignedUpException(String message) {
            super(message);
        }
    }

    /**
     * This class should be thrown when a user tries to check in to an event that they are already checked in to
     */
    public static class AlreadyCheckedInException extends Exception {
        public AlreadyCheckedInException(String message) {
            super(message);
        }
    }


    /**
     * Creates a new event and adds it to the Firestore database.
     *
     * @param userId       The UUID of the user who is creating the event.
     * @param eventName    The name of the event.
     */


    public void createEvent(String userId, String eventName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create an Event object
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("checkedInUsers", getSignedUpUsers());
        eventData.put("signedUpUsers", getCheckedInUsers());
        eventData.put("eventCreator", userId); // Use the user's UUID as the event creator

        // Add the event to Firestore
        // Add the event to a subcollection under the user's document
        db.collection("Users (latest)").document(userId).collection("Events").add(eventData)

                .addOnSuccessListener(documentReference -> {
                    // Event saved successfully
                    Log.e("Database","Event saved");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving event
                    Log.e("Database", "addEventToFirestore: Error, new event data not added to database", e);
                });
    }


}