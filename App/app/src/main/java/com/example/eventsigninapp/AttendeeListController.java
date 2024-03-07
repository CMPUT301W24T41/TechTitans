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

}
