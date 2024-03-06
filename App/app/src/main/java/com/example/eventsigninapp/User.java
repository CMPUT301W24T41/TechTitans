package com.example.eventsigninapp;

import android.net.Uri;

import java.util.Collection;
import java.util.HashSet;

public class User {

    /**
     * This variable stores the id of the user
     */
    private String id;

    /**
     * This variable stores the first name of the user
     */
    private String firstName;

    /**
     * This variable stores the last name of the user
     */
    private String lastName;

    /**
     * This variable stores the contact information of the user
     */
    private String contact;

    /**
     * This variable stores the events that the user has signed up for
     */
    private final Collection<Event> attendingEvents;

    /**
     * This variable stores the events that the user is hosting
     */
    private final Collection<Event> hostingEvents;

    /**
     * This variable stores the picture of the user
     */
    private Object picture;

    protected User() {
        attendingEvents = new HashSet<>();
        hostingEvents = new HashSet<>();
        picture = null;
        id = "";
        firstName = "";
        lastName = "";
        contact = "";
    }

    protected User(String id) {
        this();
        this.id = id;
    }

    protected User(String id, String first, String last) {
        this(id);
        this.firstName = first;
        this.lastName = last;
    }

    /**
     * This method should be used to get the id of the user
     * @return the id of the user
     */
    public String getId() {
        return id;
    }

    /**
     * This method should be used to set the id of the user
     * @param id the id of the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method should be used to get the first name of the user
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method should be used to set the first name of the user
     * @param firstName the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * This method should be used to get the last name of the user
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method should be used to set the last name of the user
     * @param lastName the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * This method should be used to get the events that the user has signed up for
     * @return the events that the user has signed up for
     */
    public Collection<Event> getAttendingEvents() {
        return attendingEvents;
    }

    /**
     * This method should be used to get the events that the user has hosted
     * @return the events that the user has hosted
     */
    public Collection<Event> getHostingEvents() {
        return hostingEvents;
    }

    /**
     * This method should be used to get the contact information of the user
     * @return the contact information of the user
     */
    public String getContact() {
        return contact;
    }

    /**
     * This method should be used to set the contact information of the user
     * @param contact the contact information of the user
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * This method should be used to sign up a user for an event
     * @param event the event to sign up for
     */
    public void checkIn(Event event) {
        try {
            event.checkInUser(this);              // inform event that user has checked in
        } catch (Event.AlreadyCheckedInException e) { // catch exception
            System.out.println(e.getMessage());       // print error message
        }
    }

    /**
     * This method should be used to sign up a user for an event
     * @param event the event to sign up for
     */
    public void signUp(Event event) {
        //TODO: implement handling of full event, ideally prevent calling of method if event is full
        if (event.isFull()) {
            System.out.println("Event is full"); // print error message
            return;
        }

        try {
            event.signUpUser(this); // inform event that user has signed up
            attendingEvents.add(event);          // add event to user's list of events
        } catch (Event.EventFullException | Event.AlreadySignedUpException e) { // catch exception
            System.out.println(e.getMessage()); // print error message
        }
    }


    public Uri getPicture() {
        return Picture;
    }

    public void setPicture(Uri picture) {
        Picture = picture;
    }
}
