package com.example.eventsigninapp;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.mockito.Mock;

public class DatabaseControllerTest {
    @Mock
    private Query mockQuery;

    @Mock
    private Task<QuerySnapshot> mockQueryTask;

    @Mock
    private Task<Uri> mockUriTask;


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
    private DatabaseController.ImageUriCallback mockImageUriCallback;

    @Mock
    private DatabaseController.UserCallback mockUserCallback;


    @Mock
    private FirebaseFirestore mockFirestore;


    @Mock
    private Task<Void> mockVoidTask;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private DocumentReference mockDocumentReference;


}