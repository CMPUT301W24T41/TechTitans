package com.example.eventsigninapp;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * This class tests the Attendee class.
 */
public class AttendeeTest {
    /**
     * This method creates a mock Attendee object.
     * @return a mock Attendee object
     */
    private Attendee mockAttendee() {
        return new Attendee("1");
    }

    /**
     * This method creates a mock Event object.
     * @return a mock Event object
     */
    private Event mockEvent() {
        return new Event();
    }

    /**
     * This method tests the getContact and setContact method of the Attendee class.
     */
    @Test
    public void testGetSetContact() {
        Attendee attendee = new Attendee("1");

        assertNull(attendee.getContact()); // should be null

        attendee.setContact("xxx");

        assertNotNull(attendee.getContact()); // should not be null
        assertEquals("xxx", attendee.getContact()); // should be "xxx"

        attendee.setContact("yyy");
        assertEquals("yyy", attendee.getContact()); // should be "yyy"
    }

    /**
     * This method tests the checkIn method of the Attendee class.
     */
    @Test
    public void testCheckIn() {
        Attendee attendee = mockAttendee();
        Event event = mockEvent();
        attendee.checkIn(event);

        assertEquals(1, event.getCheckedInAttendees().size()); // should have 1 attendee
        assertTrue(event.getCheckedInAttendees().contains(attendee)); // should contain the attendee
    }

    /**
     * This method tests the getEvents method of the Attendee class.
     */
    @Test
    public void testGetEvents() {
        Attendee attendee = new Attendee("1");

        assertNotNull(attendee.getEvents()); // should not be null
        assertEquals(0, attendee.getEvents().size()); // should be empty
    }

    /**
     * This method tests the signUp method of the Attendee class.
     */
    @Test
    public void testSignUp() {
        Attendee attendee = mockAttendee();
        Event event = mockEvent();
        attendee.signUp(event);

        assertEquals(1, attendee.getEvents().size()); // should have 1 event
        assertTrue(attendee.getEvents().contains(event)); // should contain the event
    }
}
