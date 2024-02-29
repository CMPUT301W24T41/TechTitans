package com.example.eventsigninapp;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This class tests the Event class.
 */
public class EventTest {
    /**
     * This method creates a mock Attendee object.
     * @return a mock Attendee object
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
            User mockAttendee = mockUser();
            mockAttendee.setId(String.valueOf(i));
            try {
                event.signUpAttendee(mockUser());
            } catch (Event.EventFullException e) {
                fail("Should not throw EventFullException");
            } catch (Event.AlreadySignedUpException e) {
                fail("Should not throw AlreadySignedUpException");
            }
        }

        assertTrue(event.isFull()); // should be full
    }

    /**
     * This method tests the checkInAttendee method of the Event class.
     */
    @Test
    public void testCheckInAttendee() {
        Event event = mockEvent();
        User user = mockUser();

        try {
            event.checkInAttendee(user);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInAttendees().size()); // should have 1 attendee
        assertTrue(event.getCheckedInAttendees().contains(user)); // should contain the attendee

        try {
            event.checkInAttendee(user);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInAttendees().size()); // should still have 1 attendee
    }

    /**
     * This method tests the getCheckedInAttendees method of the Event class.
     */
    @Test
    public void testGetCheckedInAttendees() {
        Event event = new Event();

        assertNotNull(event.getCheckedInAttendees()); // should not be null
        assertEquals(0, event.getCheckedInAttendees().size()); // should be empty

        User attendee = mockUser();
        attendee.setId(String.valueOf(1));

        // check in an attendee
        try {
            event.checkInAttendee(attendee);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInAttendees().size()); // should have 1 attendee
        assertTrue(event.getCheckedInAttendees().contains(attendee)); // should contain the attendee

        // check in the same attendee again
        try {
            event.checkInAttendee(attendee);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertEquals(1, event.getCheckedInAttendees().size()); // should still have 1 attendee

        // check in another attendee
        User anotherAttendee = mockUser();
        anotherAttendee.setId(String.valueOf(2));

        try {
            event.checkInAttendee(anotherAttendee);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(2, event.getCheckedInAttendees().size()); // should have 2 attendees
        assertTrue(event.getCheckedInAttendees().contains(anotherAttendee)); // should contain the other attendee
        assertTrue(event.getCheckedInAttendees().contains(attendee)); // should still contain the first attendee
    }

    /**
     * This method tests the isAttendeeCheckedIn method of the Event class.
     */
    @Test
    public void testIsAttendeeCheckedIn() {
        Event event = mockEvent();
        User user = mockUser();

        // check in the user
        try {
            event.checkInAttendee(user);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertTrue(event.isAttendeeCheckedIn(user)); // should be checked in

        // check in the same user again
        try {
            event.checkInAttendee(user);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertTrue(event.isAttendeeCheckedIn(user)); // should still be checked in
    }

    /**
     * This method tests the getSignedUpAttendees method of the Event class.
     */
    @Test
    public void testGetSignedUpAttendees() {
        Event event = new Event();

        assertNotNull(event.getSignedUpAttendees()); // should not be null
        assertEquals(0, event.getSignedUpAttendees().size()); // should be empty

        // sign up an attendee
        User user = mockUser();

        try {
            event.signUpAttendee(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertEquals(1, event.getSignedUpAttendees().size()); // should have 1 attendee
        assertTrue(event.getSignedUpAttendees().contains(user)); // should contain the attendee
    }

    /**
     * This method tests the isAttendeeSignedUp method of the Event class.
     */
    @Test
    public void testIsAttendeeSignedUp() {
        Event event = mockEvent();
        User user = mockUser();

        assertFalse(event.isAttendeeSignedUp(user)); // should not be signed up

        // sign up the user
        try {
            event.signUpAttendee(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.isAttendeeSignedUp(user)); // should be signed up
    }

    @Test
    public void testSignUpAttendee() {
        Event event = mockEvent();
        User user = mockUser();

        try {
            event.signUpAttendee(user);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpAttendees().contains(user)); // should contain the user
        assertEquals(1, event.getSignedUpAttendees().size()); // should have 1 user

        // sign up the same user again
        try {
            event.signUpAttendee(user);
            fail("Should throw AlreadySignedUpException");
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            assertNotNull(e);
        }

        assertTrue(event.getSignedUpAttendees().contains(user)); // should still contain the user
        assertEquals(1, event.getSignedUpAttendees().size()); // should still have 1 user

        // sign up another user
        User anotherUser = mockUser();

        try {
            event.signUpAttendee(anotherUser);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpAttendees().contains(anotherUser)); // should contain the other user
        assertEquals(2, event.getSignedUpAttendees().size()); // should have 2 attendees

        // sign up another user when event is full
        event.setCapacity(2);
        User fullAttendee = mockUser();

        try {
            event.signUpAttendee(fullAttendee);
            fail("Should throw EventFullException");
        } catch (Event.EventFullException e) {
            assertNotNull(e);
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertFalse(event.getSignedUpAttendees().contains(fullAttendee)); // should not contain the full user
        assertEquals(2, event.getSignedUpAttendees().size()); // should still have 2 attendees
    }
}