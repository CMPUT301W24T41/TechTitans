package com.example.eventsigninapp;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the Event class.
 */
public class EventTest {
    /**
     * This method creates a mock Attendee object.
     * @return a mock Attendee object
     */
    private Attendee mockAttendee() {
        return new Attendee();
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
            Attendee mockAttendee = mockAttendee();
            mockAttendee.setId(i);
            try {
                event.signUpAttendee(mockAttendee());
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
        Attendee attendee = mockAttendee();

        try {
            event.checkInAttendee(attendee);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertEquals(1, event.getCheckedInAttendees().size()); // should have 1 attendee
        assertTrue(event.getCheckedInAttendees().contains(attendee)); // should contain the attendee

        try {
            event.checkInAttendee(attendee);
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

        Attendee attendee = mockAttendee();
        attendee.setId(1);

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
        Attendee anotherAttendee = mockAttendee();
        anotherAttendee.setId(2);

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
        Attendee attendee = mockAttendee();

        // check in the attendee
        try {
            event.checkInAttendee(attendee);
        } catch (Event.AlreadyCheckedInException e) {
            fail("Should not throw AlreadyCheckedInException");
        }

        assertTrue(event.isAttendeeCheckedIn(attendee)); // should be checked in

        // check in the same attendee again
        try {
            event.checkInAttendee(attendee);
            fail("Should throw AlreadyCheckedInException");
        } catch (Event.AlreadyCheckedInException e) {
            assertNotNull(e);
        }

        assertTrue(event.isAttendeeCheckedIn(attendee)); // should still be checked in
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
        Attendee attendee = mockAttendee();

        try {
            event.signUpAttendee(attendee);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertEquals(1, event.getSignedUpAttendees().size()); // should have 1 attendee
        assertTrue(event.getSignedUpAttendees().contains(attendee)); // should contain the attendee
    }

    /**
     * This method tests the isAttendeeSignedUp method of the Event class.
     */
    @Test
    public void testIsAttendeeSignedUp() {
        Event event = mockEvent();
        Attendee attendee = mockAttendee();

        assertFalse(event.isAttendeeSignedUp(attendee)); // should not be signed up

        // sign up the attendee
        try {
            event.signUpAttendee(attendee);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.isAttendeeSignedUp(attendee)); // should be signed up
    }

    @Test
    public void testSignUpAttendee() {
        Event event = mockEvent();
        Attendee attendee = mockAttendee();

        try {
            event.signUpAttendee(attendee);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpAttendees().contains(attendee)); // should contain the attendee
        assertEquals(1, event.getSignedUpAttendees().size()); // should have 1 attendee

        // sign up the same attendee again
        try {
            event.signUpAttendee(attendee);
            fail("Should throw AlreadySignedUpException");
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            assertNotNull(e);
        }

        assertTrue(event.getSignedUpAttendees().contains(attendee)); // should still contain the attendee
        assertEquals(1, event.getSignedUpAttendees().size()); // should still have 1 attendee

        // sign up another attendee
        Attendee anotherAttendee = mockAttendee();

        try {
            event.signUpAttendee(anotherAttendee);
        } catch (Event.EventFullException e) {
            fail("Should not throw EventFullException");
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertTrue(event.getSignedUpAttendees().contains(anotherAttendee)); // should contain the other attendee
        assertEquals(2, event.getSignedUpAttendees().size()); // should have 2 attendees

        // sign up another attendee when event is full
        event.setCapacity(2);
        Attendee fullAttendee = mockAttendee();

        try {
            event.signUpAttendee(fullAttendee);
            fail("Should throw EventFullException");
        } catch (Event.EventFullException e) {
            assertNotNull(e);
        } catch (Event.AlreadySignedUpException e) {
            fail("Should not throw AlreadySignedUpException");
        }

        assertFalse(event.getSignedUpAttendees().contains(fullAttendee)); // should not contain the full attendee
        assertEquals(2, event.getSignedUpAttendees().size()); // should still have 2 attendees
    }
}