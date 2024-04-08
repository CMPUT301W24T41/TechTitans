package com.example.eventsigninapp;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.DownloadManager;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.concurrent.Executor;

import io.grpc.Context;

@RunWith(RobolectricTestRunner.class)
//@Config(sdk = {Config.OLDEST_SDK, Config.NEWEST_SDK})
public class DatabaseControllerTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private FirebaseStorage mockStorage;

    @Mock
    private StorageReference mockStorageReference;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private DocumentReference mockDocumentReference;

    @Mock
    private Query mockQuery;

    @Mock
    private QuerySnapshot mockSnapshot;
    @Mock
    private Task<QuerySnapshot> mockSnapTask;

    @Captor
    private ArgumentCaptor<OnCompleteListener<QuerySnapshot>> listenerCaptor;

    @Mock
    private Uri pictureUri;

    @Mock
    private User user;

    @Mock
    private UploadTask uploadTask;

    @Mock
    private ArgumentCaptor<OnSuccessListener<UploadTask.TaskSnapshot>> successListenerCaptor;

    @Mock
    private ArgumentCaptor<OnFailureListener> failuerListenerCaptor;


    private DatabaseController databaseController;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        databaseController = new DatabaseController(mockFirestore, mockStorage);

    }

    @Test
    public void testPutUserToFirestore() {
        // Arrange
        User testUser = new User("testId", "John", "Doe", "testContact", "token", new ArrayList<>(), new ArrayList<>(), false, false);

        Map<String, Object> expectedUserData = new HashMap<>();
        expectedUserData.put("id", "testId");
        expectedUserData.put("firstName", "John");
        expectedUserData.put("lastName", "Doe");
        expectedUserData.put("contact", "testContact");
        expectedUserData.put("attendingEvents", new ArrayList<>());
        expectedUserData.put("hostingEvents", new ArrayList<>());
        expectedUserData.put("admin", false);
        expectedUserData.put("profileSet", false);
        expectedUserData.put("homepage", "token");
        expectedUserData.put("fcmToken", "");

        CollectionReference mockCollection = mock(CollectionReference.class);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);

        when(mockFirestore.collection(any())).thenReturn(mockCollection);
        when(mockCollection.document(any())).thenReturn(mockDocumentReference);

        //when(mockDocumentReference.set(any(), any())).thenReturn(null);
        Task<Void> mockTask = mock(Task.class);
        when(mockDocumentReference.set(any(), any())).thenReturn(mockTask);


        databaseController.putUserToFirestore(testUser);

        verify(mockDocumentReference).set(expectedUserData, SetOptions.merge());


    }


    @Test
    public void testGetUserFromFirestore() {


        String userId = "testId";
        DatabaseController.UserCallback mockCallback = mock(DatabaseController.UserCallback.class);
        CollectionReference mockCollection = mock(CollectionReference.class);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);

        when(mockFirestore.collection(any())).thenReturn(mockCollection);
        when(mockCollection.document(any())).thenReturn(mockDocumentReference);
        when(mockCollection.whereEqualTo("id", userId)).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockSnapTask);
        when(mockSnapTask.isSuccessful()).thenReturn(true);
        when(mockSnapTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocumentSnapshot));
        when(mockDocumentSnapshot.getString("firstName")).thenReturn("John");
        when(mockDocumentSnapshot.getString("lastName")).thenReturn("Doe");
        when(mockDocumentSnapshot.getString("contact")).thenReturn("testContact");
        when(mockDocumentSnapshot.get("attendingEvents")).thenReturn(new ArrayList<>());
        when(mockDocumentSnapshot.get("hostingEvents")).thenReturn(new ArrayList<>());
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.getData()).thenReturn(Collections.emptyMap());

//        ArgumentCaptor<OnCompleteListener<QuerySnapshot>> mockListener = ArgumentCaptor.forClass(OnCompleteListener.class);
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);

            listener.onComplete(mockSnapTask);

            return null;
        }).when(mockSnapTask).addOnCompleteListener(listenerCaptor.capture());


        databaseController.getUserFromFirestore(userId, mockCallback);

        verify(mockSnapTask).isSuccessful();
    }


}