package com.example.eventsigninapp;

import android.net.Uri;

import java.util.Collection;
import java.util.HashSet;

public class User {

    private final static String profBaseUrl = "gs://eventsigninapp-2ec69.appspot.com/profile_pictures/";
    private String id;
    private String firstName;
    private String lastName;

    private String contact;

    // A collection of events that the attendee has signed up for
    private Collection<Event> attendingEvents;

    private Collection<Event> hostedEvents;


    private String imgUrl;
    private Uri Picture;


    protected User() {
    }

    protected User(String id) {
        this.id = id;
        this.firstName = "";
        this.lastName = "";
        this.contact = "";
        this.attendingEvents = new HashSet<>();
        this.imgUrl = profBaseUrl + id;
    }

    protected User(String id, String first, String last) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.contact = "";
        this.attendingEvents = new HashSet<>();
        this.imgUrl = profBaseUrl + id;
    }

    protected User(String id, String first, String last, String contact) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.contact = contact;
        this.attendingEvents = new HashSet<>();
        this.imgUrl = profBaseUrl + id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {this.id = id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<Event> getAttendingEvents() {
        return attendingEvents;
    }


    public Collection<Event> getHostedEvents() {
        return hostedEvents;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String picture) {
        this.imgUrl = picture;
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
        return attendingEvents;
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
            attendingEvents.add(event);          // add event to attendee's list of events
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
