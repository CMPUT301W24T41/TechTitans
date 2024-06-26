package com.example.eventsigninapp;

import android.util.Log;

public class EventController {
    private final Event event;

    public EventController(Event event) {
        this.event = event;
    }

    public void checkInUser(String uuid) throws AlreadyCheckedInException {
        if (event.isUserCheckedIn(uuid)) {
            event.increaseCheckedInCount(uuid);
        }
        else {
            event.increaseCheckedInCount(uuid);         // Setting checked in count to 1
            event.addCheckedInUser(uuid);

        }

    }

    public void signUpUser(String uuid) throws EventFullException, AlreadySignedUpException {
        if (event.isFull() || event.isCapped()) {
            throw new EventFullException("Event is full");
        }

        if (event.isUserSignedUp(uuid)) {
            throw new AlreadySignedUpException("Attendee is already signed up for the event");
        }

        event.addSignedUpUser(uuid);
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