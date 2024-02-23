package com.example.eventsigninapp;

public class Attendee extends User{
    private String contact;
    //for events you have signed up for
    private Object events;
    private Object homepage;
    private Object picture;

    protected Attendee(String id) {
        super(id);
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
