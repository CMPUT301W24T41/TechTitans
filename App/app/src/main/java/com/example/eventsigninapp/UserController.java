package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.UUID;



/**
 * This class should help control and fetch the users unique UID and acquire
 * their information from the the database
 */
public class UserController {


    // this represents the current user
    private static User user = new User();


    //this represents the key to the default id of the user
    private static final String deafaultUUID = "UUID_Default";

    //this represents the name to the preference that stores the id
    private static final String prefName = "ID";




    /**
     * getter for acquiring locally stored user from the controller,
     * a user must be loaded first or this function throws an error
     * @return the current user of the controller
     */
    public User getUser() {
        return user;
    }


    /**
     * setter for setting locally stored user for the controller
     */
    public void setUser(User user) {
        UserController.user = user;
    }


    /**
     * Gets the user's UUID if it exists, otherwise generates a new one and saves it
     * by using a shared preference from the context provided
     *
     * @param context: the target context to pull the shared preference from, this is intended to be used in MainActivity
     * @return A string representing the User's UUID
     */
    public String getUserID(Context context) {
        //Gets a Shared Preference for correctly logging in the user
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String uuidString = preferences.getString(deafaultUUID, null);

        //generates a uuid if the user does not have a uuid
        if (uuidString == null) {
            uuidString = UUID.randomUUID().toString();
            //addUserToFirestore(uuidString, "attendee");
            saveUUID(context, uuidString);
        }


        //returns the uuid of the user
        return uuidString;
    }

    /**
     * Updates the users UUID in the shared preference of the provided context,
     * saving it for the next time the app is opened
     * @param context: the target context
     * @param id:      the new string value of the id
     */
    public void saveUUID(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences("ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(deafaultUUID, id);
        editor.apply();
    }



    /**
     * This method edits the parameters of each of the users profile information
     * and updates the database with the new info
     *
     * @param firstName the new first name
     * @param lastName  the new last name
     * @param contact   the new contact information
     * @param pictureUri the new URI of the profile picture
     */
    public void editProfile(String firstName, String lastName, String contact, Uri pictureUri) {
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }

        if (contact != null) {
            user.setContact(contact);
        }

        if (pictureUri != null) {
            user.setPicture(pictureUri);
        }

    }

    /**
     *
     * This method should be used to sign up a user for an event
     * May be outdated/unnecessary for use
     * @param event the event to sign up for
     */
    public void checkIn(Event event) {
        EventController eventController = new EventController(event);

        try {
            eventController.checkInUser(user.getId());              // inform event that user has checked in
        } catch (EventController.AlreadyCheckedInException e) { // catch exception
            System.out.println(e.getMessage());       // print error message
        }
    }

    /**
     * This method should be used to sign up a user for an event
     * May be outdated/unnecessary for use
     * @param event the event to sign up for
     */
    public void signUp(Event event) {
        EventController eventController = new EventController(event);
        //TODO: implement handling of full event, ideally prevent calling of method if event is full
        if (event.isFull()) {
            System.out.println("Event is full"); // print error message
            return;
        }

        try {
            eventController.signUpUser(this.getUser().getId());
            this.getUser().getAttendingEvents().add(event.getUuid());          // add event to user's list of events
        } catch (EventController.EventFullException | EventController.AlreadySignedUpException e) { // catch exception
            System.out.println(e.getMessage()); // print error message
        }
    }



    /**
     * This creates a instance of imagepicker when called in the given activity
     * @param activity the activity that calls the imagepicker
     * ImagePicker library by Dhaval Sodha Parmar
     * Github: github.com/dhaval2404/imagePicker
     */
    public static void selectImage(Activity activity) {
        ImagePicker.with(activity)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }


    /**
     * This creates a instance of imagepicker when called in the given fragment
     * @param fragment the fragment that calls the imagepicker
     * ImagePicker library by Dhaval Sodha Parmar
     * Github: github.com/dhaval2404/imagePicker
     */
    public static void selectImage(Fragment fragment){
        ImagePicker.with(fragment)
                .crop()
                .compress(1024)
                .maxResultSize(1028, 1028)
                .start();
    }
}

