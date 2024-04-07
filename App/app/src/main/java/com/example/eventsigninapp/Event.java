package com.example.eventsigninapp;


import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Event implements Serializable {
    private String uuid;
    private String name;

    // The capacity of the event, 0 if uncapped
    private int capacity;
    private Collection<String> signedUpUsersUUIDs; // collection of signed up users
    private Collection<String> checkedInUsersUUIDs; // collection of checked in users
    private Uri posterUri;
    private Uri checkInQRCodeUri;
    private Uri descriptionQRCodeUri;
    private GeoPoint location;
    private final Date date;
    private String creatorUUID;
    private String description;
    private String eventCheckInQrCodeString;
    private String eventDetailsQrCodeString;

    public Event() {
        //TODO: generate a unique id on creation
        uuid = UUID.randomUUID().toString();
        name = "";
        checkedInUsersUUIDs = new ArrayList<String>();
        signedUpUsersUUIDs = new ArrayList<String>();
        posterUri = null;
        checkInQRCodeUri = null;
        eventCheckInQrCodeString = UUID.randomUUID().toString();
        eventDetailsQrCodeString = UUID.randomUUID().toString();
        location = null;
        date = null;
        capacity = 0;
    }

    public Event(String creatorUUID) {
        this();

        this.creatorUUID = creatorUUID;
    }

    public Event(String uuid, String name, String creatorUUID, int capacity) {
        this();
        this.uuid = uuid;
        this.name = name;
        this.creatorUUID = creatorUUID;
        this.capacity = capacity;
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

    public GeoPoint getLocation() {
        return this.location;
    }

    /**
     * This method should be used to set the location of the event
     * @param location the name of the event
     */
    public void setLocation(GeoPoint location) {
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
     * This method adds a user to the checked in users list
     * @param uuid the uuid of the user to check in
     */
    public void addCheckedInUser(String uuid) {
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
     * This method should be used to add a user to the signed up users list
     * @param uuid the uuid of the user to sign up
     */
    public void addSignedUpUser(String uuid) {
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
        eventMap.put("eventCheckInQrCodeString", eventCheckInQrCodeString);
        eventMap.put("eventDetailsQrCodeString", eventDetailsQrCodeString);
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

    public String getEventDetailsQrCodeString() {
        return eventDetailsQrCodeString;
    }

    public void setEventDetailsQrCodeString(String eventDetailsQrCodeString) {
        this.eventDetailsQrCodeString = eventDetailsQrCodeString;
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

    public void setDetailsQRCodeUri(Uri descriptionQRCodeUri) {
        this.descriptionQRCodeUri = descriptionQRCodeUri;
    }

    public Uri getDescriptionQRCodeUri() {
        return descriptionQRCodeUri;
    }

    @Override
    public boolean equals(Object o) {
        // check if object is an event
        if (!(o instanceof Event)) {
            return false;
        }

        if (!Objects.equals(((Event) o).getUuid(), this.getUuid())) {
            return false;
        }

        return this.toMap().equals(((Event) o).toMap());
    }

    public boolean isSameEvent(Event event) {
        return this.getUuid().equals(event.getUuid());
    }

    public String getEventCheckInQrCodeString() {
        return eventCheckInQrCodeString;
    }

    public void setEventCheckInQrCodeString(String eventCheckInQrCodeString) {
        this.eventCheckInQrCodeString = eventCheckInQrCodeString;
    }

    public void setCheckedInUsersUUIDs(ArrayList<String> checkedInUsers) {
        this.checkedInUsersUUIDs = checkedInUsers;
    }

    public void setSignedUpUsersUUIDs(ArrayList<String> signedUpUsers) {
        this.signedUpUsersUUIDs = signedUpUsers;
    }
}