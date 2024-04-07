package com.example.eventsigninapp;

public class EventController {
    private final Event event;

    public EventController(Event event) {
        this.event = event;
    }

    public void checkInUser(String uuid) throws AlreadyCheckedInException {
        if (event.isUserCheckedIn(uuid)) {
            throw new AlreadyCheckedInException("Attendee is already checked in to the event");
        }

        event.addCheckedInUser(uuid);
        event.increaseCheckedInCount(uuid);
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