package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

@RunWith(RobolectricTestRunner.class)
public class UserIdControllerTest {
    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private StorageReference mockStorageReference;

    @Mock
    private FirebaseStorage mockFirebaseStorage;

    @Mock
    private ImagePicker mockImagePicker;

    @Mock
    private UserIdController.ImageUriCallback mockImageUriCallback;

    @Mock
    private UserIdController.userCallback mockUserCallback;

    private UserIdController userIdController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userIdController = new UserIdController();
    }

    @Test
    public void testGetUserID_existingUUID() {

        String existingUUID = "existingUUID";
        //mock the process of acquiring a UUID
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_KEY", null)).thenReturn(existingUUID);

        //Perform this using the function
        String result = userIdController.getUserID(mockContext);

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
        String result = userIdController.getUserID(mockContext);

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
        userIdController.saveUUID(mockContext, uuid);

        // see if the result is added then saved and committed
        verify(mockEditor).putString("UUID_KEY", uuid);
        verify(mockEditor).apply();
    }
}