package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.eventsigninapp.UserIDController;

@RunWith(MockitoJUnitRunner.class)
public class UserIDControllerTest {

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    private UserIDController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        controller = new UserIDController();
    }

    @Test
    public void testGetUserIDWhenStored() {
        // Arrange (setup)
        String storedUUID = "StoredUUID";
        when(mockSharedPreferences.getString(anyString(), isNull())).thenReturn(storedUUID);

        // Act
        String result = controller.getUserID(mockContext);

        // Assert
        assertEquals(storedUUID, result);

        // Verify that saveUUID was not called since the ID was already stored
        verify(mockEditor, never()).putString(anyString(), anyString());
        verify(mockEditor, never()).apply();
    }

    @Test
    public void testGetUserIDWhenNotStored() {
        // Arrange (setup)
        when(mockSharedPreferences.getString(anyString(), isNull())).thenReturn(null);

        // Act
        String result = controller.getUserID(mockContext);

        // Verify that the result is not null
        assertEquals(UUID.fromString(result).toString(), result);

        // Verify that saveUUID was called since the ID was not stored
        verify(mockEditor).putString(anyString(), anyString());
        verify(mockEditor).apply();
    }

    @Test
    public void testSaveUUID() {
        String newUUID = "NewUUID";

        controller.saveUUID(mockContext, newUUID);


        // Verify that putString and apply were called
        verify(mockEditor).putString(anyString(), eq(newUUID));
        verify(mockEditor).apply();
    }
}