package com.example.eventsigninapp;

import java.util.ArrayList;

public class Admin extends User{
    protected Admin(String id) {
        super(id);
    }

    protected Admin(String id, String firstName, String lastName, String contact, ArrayList<String> attendingEvents, ArrayList<String> hostingEvents) {
        super(id, firstName, lastName, contact, attendingEvents, hostingEvents);
    }

    public Boolean isAdmin() {
        return true;
    }
}
