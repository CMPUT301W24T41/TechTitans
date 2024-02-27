package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.eventsigninapp.UserIDController;

public class UserIDControllerTest {

    @Mock
    Context mockContext;

    @Mock
    SharedPreferences mockPreferences;

    @Mock
    SharedPreferences.Editor mockEditor;

    private UserIDController userIDController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userIDController = new UserIDController();
    }

    @Test
    public void testGetUserID_existingUUID() {

        String existingUUID = "existingUUID";
        //mock the process of acquiring a UUID
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_KEY", null)).thenReturn(existingUUID);

        //Perform this using the function
        String result = userIDController.getUserID(mockContext);

        //check if we acquire the same result
        assertEquals(existingUUID, result);
    }

    @Test
    public void testGetUserID_generateUUID() {
        //mock the results of creating and generating a UUID
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_KEY", null)).thenReturn(null);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // check if the function performs the necessary action
        String result = userIDController.getUserID(mockContext);

        // see if the the result was saved to the preference
        verify(mockPreferences.edit()).putString("UUID_KEY", result);
    }

    @Test
    public void testSaveUUID() {
        //mock the process of saving a result to the preference
        String uuid = "testUUID";
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // attempt to do so with the function
        userIDController.saveUUID(mockContext, uuid);

        // see if the result is added then saved and committed
        verify(mockEditor).putString("UUID_KEY", uuid);
        verify(mockEditor).apply();
    }
}