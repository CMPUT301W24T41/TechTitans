package com.example.eventsigninapp;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AttendeeListController {
    DatabaseController dbController;
    UserController userController;
    Event event;
    ArrayList<User> signedUpUsers = new ArrayList<User>();
    ArrayList<User> checkedInUsers = new ArrayList<User>();
    public AttendeeListController() {
        dbController = new DatabaseController();
        userController = new UserController();
    }

    public AttendeeListController(Event event) {
        this.event = event;
        dbController = new DatabaseController();
        userController = new UserController();
    }

    public AttendeeListController(DatabaseController dbController, UserController userController) {
        this.dbController = dbController;
        this.userController = userController;
    }

    public ArrayList<User> getSignedUpUsers() {
        return signedUpUsers;
    }

    public ArrayList<User> getCheckedInUsers() {
        return checkedInUsers;
    }

    /**
     * This function puts a user into an array if they are checked into an event.
     * @param userIDs the ID of the user that checked in
     */
    public void updateCheckedInUsers(ArrayList<?> userIDs) {
        checkedInUsers.clear();
        for (int i = 0; i < userIDs.size(); i++) {
            userController.getUserFromFirestore((String) userIDs.get(i));
            checkedInUsers.add(userController.getUser());
        }
    }

    /**
     * This function puts a user into an array if they are signed up to an event.
     * @param userIDs the ID of the user that is signed up
     */
    public void updateSignedUpUsers(ArrayList<?> userIDs) {
        signedUpUsers.clear();
        for (int i = 0; i < userIDs.size(); i++) {
            userController.getUserFromFirestore((String) userIDs.get(i));
            signedUpUsers.add(userController.getUser());
        }
    }

}
