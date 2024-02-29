package com.example.eventsigninapp;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Event {
    private int id;
    private String name;

    // The capacity of the event, 0 if uncapped
    private int capacity;
    private Collection<User> signedUpAttendees; // collection of signed up attendees
    private Collection<User> checkedInAttendees; // collection of checked in attendees
    private Object eventPoster;
    private Object Location;
    private Date date;

    public Event() {
        //TODO: generate a unique id on creation
        id = 0;
        checkedInAttendees = new HashSet<User>();
        signedUpAttendees = new HashSet<User>();
        capacity = 0;
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
        return isCapped() && signedUpAttendees.size() >= capacity;
    }

    /**
     * This method should be used to check in an attendee for an event
     * @param attendee the attendee to check in
     * @throws AlreadyCheckedInException if the attendee is already checked in to the event
     */
    public void checkInAttendee(User attendee) throws AlreadyCheckedInException {
        if (checkedInAttendees.contains(attendee)) {
            throw new AlreadyCheckedInException("Attendee is already checked in to the event");
        }

        this.checkedInAttendees.add(attendee);
    }

    /**
     * This method should be used to get the checked in attendees for an event
     *
     * @return the checked in attendees for the event
     */
    public Collection<User> getCheckedInAttendees() {
        return checkedInAttendees;
    }

    /**
     * This method should be used to check if an attendee is checked in for an event
     * @return a boolean indicating if the attendee is checked in for the event
     */
    public boolean isAttendeeCheckedIn(User attendee) {
        return checkedInAttendees.contains(attendee);
    }

    /**
     * This method should be used to sign up an attendee for an event
     * @param attendee the attendee to sign up
     * @throws EventFullException if the event is full
     * @throws AlreadySignedUpException if the attendee is already signed up for the event
     */
    public void signUpAttendee(User attendee) throws EventFullException, AlreadySignedUpException {
        if (isCapped() && signedUpAttendees.size() >= capacity) {
            throw new EventFullException("Event is full");
        }

        if (isAttendeeSignedUp(attendee)) {
            throw new AlreadySignedUpException("Attendee is already signed up for the event");
        }

        signedUpAttendees.add(attendee);
    }

    /**
     * This method should be used to get the signed up attendees for an event
     * @return the signed up attendees for the event
     */
    public Collection<User> getSignedUpAttendees() {
        return signedUpAttendees;
    }

    /**
     * This method should be used to check if an attendee is signed up for an event
     * @return the signed up attendees for the event
     */
    public boolean isAttendeeSignedUp(User attendee) {
        return signedUpAttendees.contains(attendee);
    }

    /**
     * This class should be thrown when an attendee tries to sign up for an event that is full
     */
    public static class EventFullException extends Exception {
        public EventFullException(String message) {
            super(message);
        }
    }

    /**
     * This class should be thrown when an attendee tries to sign up for an event that they are already signed up for
     */
    public static class AlreadySignedUpException extends Exception {
        public AlreadySignedUpException(String message) {
            super(message);
        }
    }

    /**
     * This class should be thrown when an attendee tries to check in to an event that they are already checked in to
     */
    public static class AlreadyCheckedInException extends Exception {
        public AlreadyCheckedInException(String message) {
            super(message);
        }
    }
}