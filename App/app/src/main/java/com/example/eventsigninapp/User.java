package com.example.eventsigninapp;

public abstract class User {
    private int id;
    private String firstName;
    private String lastName;

    protected User() {
        this.id = 0;
        firstName = "";
        lastName = "";
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
