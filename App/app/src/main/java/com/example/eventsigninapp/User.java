package com.example.eventsigninapp;

public abstract class User {
    private String id;
    private String firstName;
    private String lastName;

    protected User() {
        this(String.valueOf(0));
    }

    protected User(String id) {
        this.id = id;
        firstName = "";
        lastName = "";
    }

    protected User(String id, String first, String last) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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










}
