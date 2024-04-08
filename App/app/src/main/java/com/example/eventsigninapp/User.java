package com.example.eventsigninapp;

import android.net.Uri;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class User implements Serializable {

    /**
     * This variable stores the URL of the all users' profile picture in Firebase Storage
     */
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
     * This variable stores the picture of the user as a Uri object
     */
    private Uri picture;

    /**
     * This variable stores the URL of the user's profile picture
     */
    private String imgUrl;

    /**
     * This variable stores the Firebase Cloud Messaging (FCM) token associated with the user
     */
    private String fcmToken;

    /**
     * This variable stores the initials of the user, which can be used as a representation if the picture is unavailable
     */
    private String initials;

    /**
     * This variable stores the URL of the user's homepage, if applicable
     */
    private String homePageUrl;

    /**
     * This variable indicates whether the user has administrative privileges
     */
    private Boolean admin;

    /**
     * This variable indicates whether the user's profile image is set or not
     */
    private Boolean profileSet;


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
        homePageUrl = "";
        fcmToken = "";
        admin = false;
        profileSet = false;
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

    protected User(String id, String first, String last, String contact, String homepage) {
        this(id, first, last);
        this.contact = contact;
        this.homePageUrl = homepage;
    }

    protected User(String id, String first, String last, String contact, String homepage, ArrayList<String> attendingEvents, ArrayList<String> hostingEvents, Boolean admin, Boolean profileSet) {
        this(id, first, last);
        this.contact = contact;
        this.homePageUrl = homepage;
        this.attendingEvents = attendingEvents;
        this.hostingEvents = hostingEvents;
        this.admin = admin;
        if (profileSet != null) {
            this.profileSet = profileSet;
        }
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
     * This method should be used to set the contact information of the user.
     *
     * @param contact the contact information of the user
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * This method retrieves the picture of the user.
     *
     * @return the URI of the user's picture
     */
    public Uri getPicture() {
        return this.picture;
    }

    /**
     * This method sets the picture of the user.
     *
     * @param picture the URI of the user's picture
     */
    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    /**
     * This method retrieves the URL of the user's picture.
     *
     * @return the URL of the user's picture
     */
    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * This method sets the URL of the user's picture.
     *
     * @param imgUrl the URL of the user's picture
     */
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * This method sets the Firebase Cloud Messaging (FCM) token associated with the user.
     *
     * @param fcmToken the FCM token
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * This method retrieves the Firebase Cloud Messaging (FCM) token associated with the user.
     *
     * @return the FCM token
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * This method checks if the user is an admin.
     *
     * @return true if the user is an admin, false otherwise
     */
    public Boolean isAdmin() {
        return admin;
    }

    /**
     * This method sets the user's admin status.
     *
     * @param admin true if the user is an admin, false otherwise
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     * This method should be used to delete the user's picture.
     */
    public void deletePicture() {
        this.picture = null;
        this.imgUrl = "";
    }

    /**
     * This method generates and sets the initials of the user.
     *
     * @return the generated initials
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
     * This method retrieves the initials of the user.
     *
     * @return the initials of the user
     */
    public String getInitials() {
        return generateInitials();
    }

    /**
     * This method retrieves the URL of the user's homepage.
     *
     * @return the URL of the user's homepage
     */
    public String getHomePageUrl() {
        return homePageUrl;
    }

    /**
     * This method sets the URL of the user's homepage.
     *
     * @param homePageUrl the URL of the user's homepage
     */
    public void setHomePageUrl(String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }

    /**
     * This method checks if the user's profile is set up.
     *
     * @return true if the user's profile is set up, false otherwise
     */
    public Boolean isProfileSet() {
        return profileSet;
    }

    /**
     * This method sets the user's profile status.
     *
     * @param profileSet true if the user's profile is set up, false otherwise
     */
    public void setProfileSet(Boolean profileSet) {
        this.profileSet = profileSet;
    }
}

