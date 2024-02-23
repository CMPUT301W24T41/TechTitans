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
        // Arrange
        String existingUUID = "existingUUID";
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_KEY", null)).thenReturn(existingUUID);

        // Act
        String result = userIDController.getUserID(mockContext);

        // Assert
        assertEquals(existingUUID, result);
    }

    @Test
    public void testGetUserID_generateUUID() {
        // Arrange
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_KEY", null)).thenReturn(null);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // Act
        String result = userIDController.getUserID(mockContext);

        // Assert
        verify(mockPreferences.edit()).putString("UUID_KEY", result);
    }

    @Test
    public void testSaveUUID() {
        // Arrange
        String uuid = "testUUID";
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // Act
        userIDController.saveUUID(mockContext, uuid);

        // Assert
        verify(mockEditor).putString("UUID_KEY", uuid);
        verify(mockEditor).apply();
    }
}