package com.example.eventsigninapp;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This class tests the Event class.
 */
public class EventTest {
    /**
     * This method creates a mock User object.
     * @return a mock User object
     */
    private User mockUser() {
        return new User("1");
    }

    /**
     * This method creates a mock Event object.
     * @return a mock Event object
     */
    private Event mockEvent() {
        return new Event();
    }

    /**
     * This method tests the getCapacity and setCapacity method of the Event class.
     */
    @Test
    public void testGetSetCapacity() {
        Event event = new Event();

        assertEquals(0, event.getCapacity()); // should be 0

        event.setCapacity(10);

        assertEquals(10, event.getCapacity()); // should be 10

        try {
            event.setCapacity(-1);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * This method tests the getId and setId method of the Event class.
     */
    @Test
    public void testGetSetId() {
        Event event = mockEvent();

        assertEquals("", event.getId()); // should be empty

        event.setId("Test");
        assertEquals("Test", event.getId()); // should be "Test"

        event.setId("Overwrite");
        assertEquals("Overwrite", event.getId()); // should be "Overwrite"
    }

    @Test
    public void testGetSetName() {
        Event event = mockEvent();

        assertEquals("", event.getName()); // should be empty

        event.setName("Test");
        assertEquals("Test", event.getName()); // should be "Test"

        event.setName("Overwrite");
        assertEquals("Overwrite", event.getName()); // should be "Overwrite"
    }

    /**
     * This method tests the isCapped method of the Event class.
     */
    @Test
    public void testIsCapped() {
        Event event = new Event();

        assertFalse(event.isCapped()); // should not be capped

        event.setCapacity(10);

        assertTrue(event.isCapped()); // should be capped
    }

    /**
     * This method tests the isFull method of the Event class.
     */
    @Test
    public void testIsFull() {
        Event event = new Event();

        assertFalse(event.isFull()); // should not be full

        event.setCapacity(3);

        assertFalse(event.isFull()); // should not be full

        for (int i = 0; i < 3; i++) {
            User mockUser = mockUser();
            mockUser.setId(String.valueOf(i));
            try {
                event.signUpUser(mockUser());
            } catch (Event.EventFullException e) {
                fail("Should not throw EventFullException");
            } catch (Event.AlreadySignedUpException e) {
                fail("Should not throw AlreadySignedUpException");
            }
        }

        assertTrue(event.isFull()); // should be full
    }

    /**
     * This method tests the checkInUser method of the Event class.
     */
    @Test
    public void testCheckInUser() {
        Event event = mockEvent();
        User user = mockUser();

        try {
            event.checkInUser(user);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInUsers().size()); // should have 1 attendee
        assertTrue(event.getCheckedInUsers().contains(user)); // should contain the attendee

        try {
            event.checkInUser(user);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInUsers().size()); // should still have 1 attendee
    }

    /**
     * This method tests the getCheckedInUsers method of the Event class.
     */
    @Test
    public void testGetCheckedInUsers() {
        Event event = new Event();

        assertNotNull(event.getCheckedInUsers()); // should not be null
        assertEquals(0, event.getCheckedInUsers().size()); // should be empty

        User attendee = mockUser();
        attendee.setId(String.valueOf(1));

        // check in an attendee
        try {
            event.checkInUser(attendee);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInUsers().size()); // should have 1 attendee
        assertTrue(event.getCheckedInUsers().contains(attendee)); // should contain the attendee

        // check in the same attendee again
        try {
            event.checkInUser(attendee);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInUsers().size()); // should still have 1 attendee

        // check in another attendee
        User anotherUser = mockUser();
        anotherUser.setId(String.valueOf(2));

        try {
            event.checkInUser(anotherUser);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(2, event.getCheckedInUsers().size()); // should have 2 attendees
        assertTrue(event.getCheckedInUsers().contains(anotherUser)); // should contain the other attendee
        assertTrue(event.getCheckedInUsers().contains(attendee)); // should still contain the first attendee
    }

    /**
     * This method tests the isUserCheckedIn method of the Event class.
     */
    @Test
    public void testIsUserCheckedIn() {
        Event event = mockEvent();
        User user = mockUser();

        // check in the user
        try {
            event.checkInUser(user);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertTrue(event.isUserCheckedIn(user)); // should be checked in

        // check in the same user again
        try {
            event.checkInUser(user);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertTrue(event.isUserCheckedIn(user)); // should still be checked in
    }

    /**
     * This method tests the getSignedUpUsers method of the Event class.
     */
    @Test
    public void testGetSignedUpUsers() {
        Event event = new Event();

        assertNotNull(event.getSignedUpUsers()); // should not be null
        assertEquals(0, event.getSignedUpUsers().size()); // should be empty

        // sign up an attendee
        User user = mockUser();

        try {
            event.signUpUser(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertEquals(1, event.getSignedUpUsers().size()); // should have 1 attendee
        assertTrue(event.getSignedUpUsers().contains(user)); // should contain the attendee
    }

    /**
     * This method tests the isUserSignedUp method of the Event class.
     */
    @Test
    public void testIsUserSignedUp() {
        Event event = mockEvent();
        User user = mockUser();

        assertFalse(event.isUserSignedUp(user)); // should not be signed up

        // sign up the user
        try {
            event.signUpUser(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.isUserSignedUp(user)); // should be signed up
    }

    @Test
    public void testSignUpUser() {
        Event event = mockEvent();
        User user = mockUser();

        try {
            event.signUpUser(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpUsers().contains(user)); // should contain the user
        assertEquals(1, event.getSignedUpUsers().size()); // should have 1 user

        // sign up the same user again
        try {
            event.signUpUser(user);
            fail("Should throw AlreadySignedUpException");
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            assertNotNull(e);
        }

        assertTrue(event.getSignedUpUsers().contains(user)); // should still contain the user
        assertEquals(1, event.getSignedUpUsers().size()); // should still have 1 user

        // sign up another user
        User anotherUser = mockUser();

        try {
            event.signUpUser(anotherUser);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpUsers().contains(anotherUser)); // should contain the other user
        assertEquals(2, event.getSignedUpUsers().size()); // should have 2 attendees

        // sign up another user when event is full
        event.setCapacity(2);
        User fullUser = mockUser();

        try {
            event.signUpUser(fullUser);
            fail("Should throw EventFullException");
        } catch (Event.EventFullException e) {
            assertNotNull(e);
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertFalse(event.getSignedUpUsers().contains(fullUser)); // should not contain the full user
        assertEquals(2, event.getSignedUpUsers().size()); // should still have 2 attendees
    }
}