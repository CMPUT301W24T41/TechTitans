package com.example.eventsigninapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
public class UserControllerTest {
    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockPreferences;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private Query mockQuery;

    @Mock
    private Task<QuerySnapshot> mockQueryTask;

    @Mock
    private Task<Uri> mockUriTask;


    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private DocumentReference mockDocumentReference;

    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    @Mock
    private StorageReference mockStorageReference;

    @Mock
    private Uri mockUri;


    @Mock
    private FirebaseStorage mockFirebaseStorage;

    @Mock
    private UploadTask mockUploadTask;

    @Mock
    private UserController.ImageUriCallback mockImageUriCallback;

    @Mock
    private UserController.userCallback mockUserCallback;

    private UserController userController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        FirebaseApp.initializeApp(RuntimeEnvironment.getApplication());
        userController = new UserController(mockFirestore, mockFirebaseStorage);
    }

    @Test
    public void testGetUserID_existingUUID() {

        String existingUUID = "existingUUID";
        //mock the process of acquiring a UUID
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_Default", null)).thenReturn(existingUUID);

        //Perform this using the function
        String result = userController.getUserID(mockContext);

        //check if we acquire the same result
        assertEquals(existingUUID, result);
    }

    @Test
    public void testGetUserID_generateUUID() {
        //mock the results of creating and generating a UUID
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.getString("UUID_Default", null)).thenReturn(null);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // check if the function performs the necessary action
        String result = userController.getUserID(mockContext);

        // see if the the result was saved to the preference
        verify(mockPreferences.edit()).putString("UUID_Default", result);
    }

    @Test
    public void testSaveUUID() {
        //mock the process of saving a result to the preference
        String uuid = "testUUID";
        when(mockContext.getSharedPreferences("ID", Context.MODE_PRIVATE)).thenReturn(mockPreferences);
        when(mockPreferences.edit()).thenReturn(mockEditor);

        // attempt to do so with the function
        userController.saveUUID(mockContext, uuid);

        // see if the result is added then saved and committed
        verify(mockEditor).putString("UUID_Default", uuid);
        verify(mockEditor).apply();
    }

    @Test
    public void testGetUserFromFirestore_existingUser() {
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(true);
        when(mockQueryTask.getResult()).thenReturn(mock(QuerySnapshot.class));
        when(mockQueryTask.getResult().getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));

        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockDocumentSnapshot.getString(anyString())).thenReturn("John");

        userController.getUserFromFirestore("existingUserID");


        assertEquals("John", userController.getUser().getFirstName());
    }

    @Test
    public void testPutUserToFirestore() {
        User mockUser = new User("1234", "Joe", "Smith", "12345");
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(anyMap())).thenReturn(mockVoidTask);

        userController.setUser(mockUser);
        userController.putUserToFirestore();

        verify(mockDocumentReference).set(anyMap());
    }

    @Test
    public void testGetOtherUserFromFirestore_existingUser() {
        // Mock the Firestore instance
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockQueryTask);
        when(mockQueryTask.isSuccessful()).thenReturn(true);

        // Mock the DocumentSnapshot
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        when(mockQueryTask.getResult()).thenReturn(mock(QuerySnapshot.class));
        when(mockDocumentSnapshot.getString(anyString())).thenReturn("Jane");

        // Perform the function
        userController.getOtherUserFromFirestore("existingUserID", mockUserCallback);

        // Verify that the callback is invoked with the correct user
        verify(mockUserCallback).onCallback(any(User.class));
    }

    @Test
    public void testEditProfile() {
        User mockUser = new User("123456", "John", "Doe", "123456");
        when(mockFirestore.collection(anyString())).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(anyMap())).thenReturn(mockVoidTask);

        userController.setUser(mockUser);

        userController.editProfile("Jane", "Smith", "789012", null);

        assertEquals("Jane", userController.getUser().getFirstName());
        assertEquals("Smith", userController.getUser().getLastName());
        assertEquals("789012", userController.getUser().getContact());
        verify(mockDocumentReference).set(anyMap());
    }

    @Test
    public void testUploadProfilePicture() {
        User mockUser = new User("1234", "Joe", "Smith", "12345");
        when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        when(mockStorageReference.child(anyString())).thenReturn(mockStorageReference);
        when(mockStorageReference.putFile(any(Uri.class))).thenReturn(mockUploadTask);
        when(mockUploadTask.addOnSuccessListener(any())).thenReturn(mockUploadTask);
        when(mockUploadTask.addOnFailureListener(any())).thenReturn(mockUploadTask);

        userController.uploadProfilePicture(mockUri);

        verify(mockStorageReference).putFile(any(Uri.class));
    }

    @Test
    public void testUpdateWithProfPictureFromWeb() {
        when(mockFirebaseStorage.getReferenceFromUrl(anyString())).thenReturn(mockStorageReference);
        when(mockStorageReference.getDownloadUrl()).thenReturn(mockUriTask);

        userController.updateWithProfPictureFromWeb();

        assertNotNull(userController.getUser().getPicture());
    }

    @Test
    public void testGetOtherUserProfilePicture() {
        when(mockFirebaseStorage.getReference()).thenReturn(mockStorageReference);
        when(mockStorageReference.child(anyString())).thenReturn(mockStorageReference);
        when(mockStorageReference.getDownloadUrl()).thenReturn(mockUriTask);

        userController.getOtherUserProfilePicture("otherUserID", mockImageUriCallback);

        verify(mockImageUriCallback).onImageUriCallback(any(Uri.class));
    }

}