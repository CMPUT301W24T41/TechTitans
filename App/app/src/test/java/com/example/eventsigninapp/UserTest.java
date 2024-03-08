package com.example.eventsigninapp;

import static org.junit.Assert.*;

import android.net.Uri;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * This class tests the User class.
 */
@RunWith(RobolectricTestRunner.class)
public class UserTest {
    /**
     * This method creates a mock User object.
     * @return a mock User object
     */
    private User mockUser() {
        return new User();
    }

    /**
     * This method creates a mock Event object.
     * @return a mock Event object
     */
    private Event mockEvent() {
        return new Event();
    }

    /**
     * This method tests the getPicture and setPicture method of the User class.
     */
    @Test
    public void testGetSetPicture() {
        User user = mockUser();

        assertNull(user.getPicture()); // should be null initially

        user.setPicture(Uri.parse("content://img.jpg"));

        assertEquals(Uri.parse("content://img.jpg"), user.getPicture());

        user.setPicture(Uri.parse("content://photo.jpg"));

        assertEquals(Uri.parse("content://photo.jpg"), user.getPicture());

    }



    /**
     * This method tests the getImgUrl and setImgUrl method of the User class.
     */
    @Test
    public void testGetSetImgUrl(){
        User user = mockUser();

        assertNotNull(user.getImgUrl()); // should not be null
        assertEquals("", user.getImgUrl()); // should be empty

        user.setImgUrl("photo.jpg");

        assertEquals("photo.jpg", user.getImgUrl());

        user.setImgUrl("image.jpg");
        assertEquals("image.jpg", user.getImgUrl());
    }

    /**
     * This method tests the getContact and setContact method of the User class.
     */
    @Test
    public void testGetSetContact() {
        User user = mockUser();

        assertNotNull(user.getContact()); // should not be null
        assertEquals("", user.getContact()); // should be empty

        user.setContact("xxx");

        assertEquals("xxx", user.getContact()); // should be "xxx"

        user.setContact("yyy");
        assertEquals("yyy", user.getContact()); // should be "yyy"
    }

    /**
     * This method tests the getFirstName and setFirstName method of the User class.
     */
    @Test
    public void testGetSetFirstName() {
        User user = mockUser();

        assertEquals("", user.getFirstName()); // should be empty

        user.setFirstName("John");
        assertEquals("John", user.getFirstName()); // should be "John"

        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName()); // should be "Jane"
    }

    /**
     * This method tests the getLastName and setLastName method of the User class.
     */
    @Test
    public void testGetSetLastName() {
        User user = mockUser();

        assertEquals("", user.getLastName()); // should be empty

        user.setLastName("Doe");
        assertEquals("Doe", user.getLastName()); // should be "Doe"

        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName()); // should be "Smith"
    }

    /**
     * This method tests the getId and setId method of the User class.
     */
    @Test
    public void testGetSetId() {
        User user = mockUser();

        assertEquals("", user.getId()); // should be empty

        user.setId("Test");
        assertEquals("Test", user.getId()); // should be "2"

        user.setId("Overwrite");
        assertEquals("Overwrite", user.getId()); // should be "3"
    }



    /**
     * This method tests the getAttendingEvents method of the User class.
     */
    @Test
    public void testGetAttendingEvents() {
        User user = mockUser();

        assertNotNull(user.getAttendingEvents()); // should not be null
        assertEquals(0, user.getAttendingEvents().size()); // should be empty
    }

    /**
     * This method tests the getHostingEvents method of the User class.
     */
    @Test
    public void testGetHostingEvents() {
        User user = mockUser();

        assertNotNull(user.getHostingEvents()); // should not be null
        assertEquals(0, user.getHostingEvents().size()); // should be empty
    }


}
