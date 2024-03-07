package com.example.eventsigninapp;


import android.net.Uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Event {
    private String uuid;
    private String name;

    // The capacity of the event, 0 if uncapped
    private int capacity;
    private final Collection<String> signedUpUsersUUIDs; // collection of signed up users
    private final Collection<String> checkedInUsersUUIDs; // collection of checked in users
    private Uri posterUri;
    private Uri checkInQRCodeUri;
    private Uri descriptionQRCodeUri;
    private Object location;
    private Date date;
    private String creatorUUID;
    private String description;

    public Event() {
        //TODO: generate a unique id on creation
        uuid = UUID.randomUUID().toString();
        name = "";
        checkedInUsersUUIDs = new ArrayList<String>();
        signedUpUsersUUIDs = new ArrayList<String>();
        posterUri = null;
        checkInQRCodeUri = null;
        location = null;
        date = null;
        capacity = 10000000;
    }

    public Event(String creatorUUID) {
        this();

        this.creatorUUID = creatorUUID;
    }

    public String getCreatorUUID() {
        return creatorUUID;
    }

    public void setCreatorUUID(String creatorUUID) {
        this.creatorUUID = creatorUUID;
    }

    /**
     * This method should be used to get the id of the event
     * @return the id of the event
     */
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
        return isCapped() && signedUpUsersUUIDs.size() >= capacity;
    }

    /**
     * This method should be used to check in a user for an event
     * @param uuid the uuid of the user to check in
     * @throws AlreadyCheckedInException if the user is already checked in to the event
     */
    public void checkInUser(String uuid) throws AlreadyCheckedInException {
        if (checkedInUsersUUIDs.contains(uuid)) {
            throw new AlreadyCheckedInException("Attendee is already checked in to the event");
        }

        checkedInUsersUUIDs.add(uuid);
    }

    /**
     * This method should be used to get the checked in users for an event
     *
     * @return the uuids of checked in users for the event
     */
    public Collection<String> getCheckedInUsersUUIDs() {
        return checkedInUsersUUIDs;
    }

    /**
     * This method should be used to check if a user is checked in for an event
     * @return a boolean indicating if the user is checked in for the event
     */
    public boolean isUserCheckedIn(String uuid) {
        return checkedInUsersUUIDs.contains(uuid);
    }

    /**
     * This method should be used to sign up a user for an event
     * @param uuid the uuid of the user to sign up
     * @throws EventFullException if the event is full
     * @throws AlreadySignedUpException if the user is already signed up for the event
     */
    public void signUpUser(String uuid) throws EventFullException, AlreadySignedUpException {
        if (isCapped() && signedUpUsersUUIDs.size() >= capacity) {
            throw new EventFullException("Event is full");
        }

        if (isUserSignedUp(uuid)) {
            throw new AlreadySignedUpException("Attendee is already signed up for the event");
        }

        signedUpUsersUUIDs.add(uuid);
    }

    /**
     * This method should be used to get the signed up users for an event
     * @return the signed up users for the event
     */
    public Collection<String> getSignedUpUsersUUIDs() {
        return signedUpUsersUUIDs;
    }

    /**
     * This method should be used to check if a user is signed up for an event
     * @return the signed up users for the event
     */
    public boolean isUserSignedUp(String uuid) {
        return signedUpUsersUUIDs.contains(uuid);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("uuid", uuid);
        eventMap.put("name", name);
        eventMap.put("creatorUUID", creatorUUID);
        eventMap.put("capacity", capacity);
        eventMap.put("date", date);
        eventMap.put("location", location);
        eventMap.put("checkedInUsers", checkedInUsersUUIDs);
        eventMap.put("signedUpUsers", signedUpUsersUUIDs);
        eventMap.put("description", description);
        return eventMap;
    }

    public void setDescription(String eventDescription) {
        this.description = eventDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setPosterUri(Uri posterUri) {
        this.posterUri = posterUri;
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public void setCheckInQRCodeUri(Uri checkInQRCodeUri) {
        this.checkInQRCodeUri = checkInQRCodeUri;
    }

    public Uri getCheckInQRCodeUri() {
        return checkInQRCodeUri;
    }

    public void setDescriptionQRCodeUri(Uri descriptionQRCodeUri) {
        this.descriptionQRCodeUri = descriptionQRCodeUri;
    }

    public Uri getDescriptionQRCodeUri() {
        return descriptionQRCodeUri;
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
}