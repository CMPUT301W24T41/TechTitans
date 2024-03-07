package com.example.eventsigninapp;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This class tests the User class.
 */
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
