package com.example.eventsigninapp;

import android.net.Uri;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class User {

    private static final String profilePicpath = "gs://eventsigninapp-2ec69.appspot.com/profile_pictures/";

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
     * This variable stores the events location if required
     */
    private String location;

    /**
     * This variable stores the events that the user has signed up for
     */
    private ArrayList<String> attendingEvents;

    /**
     * This variable stores the events that the user is hosting
     */
    private ArrayList<String> hostingEvents;

    /**
     * This variable stores the picture of the user
     */
    private Uri picture;
    private String imgUrl;

    protected User() {
        attendingEvents = new ArrayList<>();
        hostingEvents = new ArrayList<>();
        picture = null;
        id = "";
        firstName = "";
        lastName = "";
        contact = "";
        location = "";
        imgUrl = "";
    }

    protected User(String id) {
        this();
        this.id = id;
        this.imgUrl = profilePicpath + id;
    }

    protected User(String id, String first, String last) {
        this(id);
        this.firstName = first;
        this.lastName = last;
    }

    protected User(String id, String first, String last, String contact) {
        this(id, first, last);
        this.contact = contact;
    }

    protected User(String id, String first, String last, String contact, ArrayList<String> attendingEvents, ArrayList<String> hostingEvents) {
        this(id, first, last);
        this.contact = contact;
        this.attendingEvents = attendingEvents;
        this.hostingEvents = hostingEvents;
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
    public ArrayList<String> getAttendingEvents() {
        return attendingEvents;
    }

    /**
     * This method should be used to get the events that the user has hosted
     * @return the events that the user has hosted
     */
    public ArrayList<String> getHostingEvents() {
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


    public Uri getPicture() {
        return this.picture;
    }

    public void setPicture(Uri picture) {
         this.picture = picture;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
