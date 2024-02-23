package com.example.eventsigninapp;

import java.util.Collection;
import java.util.HashSet;

public class Attendee extends User {
    private String contact;

    // A collection of events that the attendee has signed up for
    private Collection<Event> events;
    private Object homepage;
    private Object picture;

    public Attendee() {
        super();
        events = new HashSet<>();
    }

    /**
     * This method should be used to get the contact information of the attendee
     * @return the contact information of the attendee
     */
    public String getContact() {
        return contact;
    }

    /**
     * This method should be used to set the contact information of the attendee
     * @param contact the contact information of the attendee
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * This method should be used to sign up an attendee for an event
     * @param event the event to sign up for
     */
    public void checkIn(Event event) {
        try {
            event.checkInAttendee(this);              // inform event that attendee has checked in
        } catch (Event.AlreadyCheckedInException e) { // catch exception
            System.out.println(e.getMessage());       // print error message
        }
    }

    /**
     * This method should be used to get the events that the attendee has signed up for
     * @return the events that the attendee has signed up for
     */
    public Collection<Event> getEvents() {
        return events;
    }

    /**
     * This method should be used to sign up an attendee for an event
     * @param event the event to sign up for
     */
    public void signUp(Event event) {
        //TODO: implement handling of full event, ideally prevent calling of method if event is full
        if (event.isFull()) {
            System.out.println("Event is full"); // print error message
            return;
        }

        try {
            event.signUpAttendee(this); // inform event that attendee has signed up
            events.add(event);          // add event to attendee's list of events
        } catch (Event.EventFullException | Event.AlreadySignedUpException e) { // catch exception
            System.out.println(e.getMessage()); // print error message
        }
    }
}