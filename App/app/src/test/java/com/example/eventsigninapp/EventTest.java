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

        // Should not be empty or null
        assertNotNull(event.getUuid());
        assertNotEquals("", event.getUuid());
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
            String id = mockUser.getId();
            try {
                event.signUpUser(id);
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
        String id = user.getId();

        try {
            event.checkInUser(id);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInUsersUUIDs().size()); // should have 1 attendee
        assertTrue(event.getCheckedInUsersUUIDs().contains(id)); // should contain the attendee

        try {
            event.checkInUser(id);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInUsersUUIDs().size()); // should still have 1 attendee
    }

    /**
     * This method tests the getCheckedInUsers method of the Event class.
     */
    @Test
    public void testGetCheckedInUsers() {
        Event event = new Event();

        assertNotNull(event.getCheckedInUsersUUIDs()); // should not be null
        assertEquals(0, event.getCheckedInUsersUUIDs().size()); // should be empty

        User user = mockUser();
        String id = user.getId();

        // check in an attendee
        try {
            event.checkInUser(id);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInUsersUUIDs().size()); // should have 1 attendee
        assertTrue(event.getCheckedInUsersUUIDs().contains(id)); // should contain the attendee

        // check in the same attendee again
        try {
            event.checkInUser(id);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInUsersUUIDs().size()); // should still have 1 attendee

        // check in another attendee
        User anotherUser = mockUser();
        String anotherId = anotherUser.getId();

        try {
            event.checkInUser(anotherId);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(2, event.getCheckedInUsersUUIDs().size()); // should have 2 attendees
        assertTrue(event.getCheckedInUsersUUIDs().contains(anotherId)); // should contain the other attendee
        assertTrue(event.getCheckedInUsersUUIDs().contains(id)); // should still contain the first attendee
    }

    /**
     * This method tests the isUserCheckedIn method of the Event class.
     */
    @Test
    public void testIsUserCheckedIn() {
        Event event = mockEvent();
        User user = mockUser();
        String id = user.getId();

        // check in the user
        try {
            event.checkInUser(id);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertTrue(event.isUserCheckedIn(id)); // should be checked in

        // check in the same user again
        try {
            event.checkInUser(id);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertTrue(event.isUserCheckedIn(id)); // should still be checked in
    }

    /**
     * This method tests the getSignedUpUsers method of the Event class.
     */
    @Test
    public void testGetSignedUpUsers() {
        Event event = new Event();

        assertNotNull(event.getSignedUpUsersUUIDs()); // should not be null
        assertEquals(0, event.getSignedUpUsersUUIDs().size()); // should be empty

        // sign up an attendee
        User user = mockUser();
        String id = user.getId();

        try {
            event.signUpUser(id);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertEquals(1, event.getSignedUpUsersUUIDs().size()); // should have 1 attendee
        assertTrue(event.getSignedUpUsersUUIDs().contains(id)); // should contain the attendee
    }

    /**
     * This method tests the isUserSignedUp method of the Event class.
     */
    @Test
    public void testIsUserSignedUp() {
        Event event = mockEvent();
        User user = mockUser();
        String id = user.getId();

        assertFalse(event.isUserSignedUp(id)); // should not be signed up

        // sign up the user
        try {
            event.signUpUser(id);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.isUserSignedUp(id)); // should be signed up
    }

    @Test
    public void testSignUpUser() {
        Event event = mockEvent();
        User user = mockUser();
        String id = user.getId();

        try {
            event.signUpUser(id);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpUsersUUIDs().contains(id)); // should contain the user
        assertEquals(1, event.getSignedUpUsersUUIDs().size()); // should have 1 user

        // sign up the same user again
        try {
            event.signUpUser(id);
            fail("Should throw AlreadySignedUpException");
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            assertNotNull(e);
        }

        assertTrue(event.getSignedUpUsersUUIDs().contains(id)); // should still contain the user
        assertEquals(1, event.getSignedUpUsersUUIDs().size()); // should still have 1 user

        // sign up another user
        User anotherUser = mockUser();
        String anotherId = anotherUser.getId();

        try {
            event.signUpUser(anotherId);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpUsersUUIDs().contains(anotherId)); // should contain the other user
        assertEquals(2, event.getSignedUpUsersUUIDs().size()); // should have 2 attendees

        // sign up another user when event is full
        event.setCapacity(2);
        User fullUser = mockUser();
        String fullId = fullUser.getId();

        try {
            event.signUpUser(fullId);
            fail("Should throw EventFullException");
        } catch (Event.EventFullException e) {
            assertNotNull(e);
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertFalse(event.getSignedUpUsersUUIDs().contains(fullId)); // should not contain the full user
        assertEquals(2, event.getSignedUpUsersUUIDs().size()); // should still have 2 attendees
    }
}