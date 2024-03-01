package com.example.eventsigninapp;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Event {
    private String id;
    private String name;

    // The capacity of the event, 0 if uncapped
    private int capacity;
    private final Collection<User> signedUpUsers; // collection of signed up users
    private final Collection<User> checkedInUsers; // collection of checked in users
    private Object eventPoster;
    private Object location;
    private Date date;

    public Event() {
        //TODO: generate a unique id on creation
        id = "";
        name = "";
        checkedInUsers = new HashSet<User>();
        signedUpUsers = new HashSet<User>();
        eventPoster = null;
        location = null;
        date = null;
        capacity = 0;
    }

    /**
     * This method should be used to get the id of the event
     * @return the id of the event
     */
    public String getId() {
        return id;
    }

    /**
     * This method should be used to set the id of the event
     * @param id the id of the event
     */
    public void setId(String id) {
        this.id = id;
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

        this.checkedInUsers.add(user);
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
}