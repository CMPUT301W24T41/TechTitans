package com.example.eventsigninapp;

import android.net.Uri;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class User implements Serializable {

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
    private final String location;

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
    private String fcmtoken;
    private String initials;

    private Boolean admin;

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
        initials = "";
        admin = false;
    }

    protected User(String id) {
        this();
        this.id = id;
        this.imgUrl = profilePicpath + id;
    }

    protected User(String id, String first, String last) {
        this(id);
        this.imgUrl = profilePicpath + id;
        this.firstName = first;
        this.lastName = last;
    }

    protected User(String id, String first, String last, String contact) {
        this(id, first, last);
        this.contact = contact;
    }

    protected User(String id, String first, String last, String contact, ArrayList<String> attendingEvents, ArrayList<String> hostingEvents, Boolean admin) {
        this(id, first, last);
        this.contact = contact;
        this.attendingEvents = attendingEvents;
        this.hostingEvents = hostingEvents;
        this.admin = admin;
    }



    /**
     * This method should be used to get the id of the user
     *
     * @return the id of the user
     */
    public String getId() {
        return id;
    }

    /**
     * This method should be used to set the id of the user
     *
     * @param id the id of the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method should be used to get the first name of the user
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method should be used to set the first name of the user
     *
     * @param firstName the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.initials = generateInitials();
    }

    /**
     * This method should be used to get the last name of the user
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method should be used to set the last name of the user
     *
     * @param lastName the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.initials = generateInitials();
    }

    /**
     * This method should be used to get the events that the user has signed up for
     *
     * @return the events that the user has signed up for
     */
    public ArrayList<String> getAttendingEvents() {
        return attendingEvents;
    }

    /**
     * This method should be used to get the events that the user has hosted
     *
     * @return the events that the user has hosted
     */
    public ArrayList<String> getHostingEvents() {
        return hostingEvents;
    }

    /**
     * This method should be used to get the contact information of the user
     *
     * @return the contact information of the user
     */
    public String getContact() {
        return contact;
    }

    /**
     * This method should be used to set the contact information of the user
     *
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

    public void setFcmToken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    ;

    public String getFcmToken() {
        return fcmtoken;
    }

    public Boolean isAdmin() {
        return admin;
    }



    /**
     * This method should be used to delete the picture of the user
     */
    public void deletePicture() {
        this.picture = null;
        this.imgUrl = "";
    }

    /**
     * This method should be used to generate and set the initials of the user
     */
    private String generateInitials() {
        StringBuilder initialsBuilder = new StringBuilder();

        if (!firstName.isEmpty()) {
            initialsBuilder.append(firstName.charAt(0));
        }
        if (!lastName.isEmpty()) {
            initialsBuilder.append(lastName.charAt(0));
        }
        initials = initialsBuilder.toString().toUpperCase();
        return initials;
    }

    /**
     * This method should be used to get the initials of the user
     * @return the initials of the user
     */
    public String getInitials() {
        return generateInitials();
    }

}