package com.example.eventsigninapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<String, Object> user = new HashMap<>();
    FirebaseFirestore db;
    private UserIDController userIDController;
    TextView idText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIDController = new UserIDController();


    }


    //Testing Database
//    @Override
//    public void onUserInteraction() {


//        super.onUserInteraction();
//
//        db = FirebaseFirestore.getInstance();
//        FirebaseApp.initializeApp(this);
//        user.put("name", "test");
//        db.collection("testDoc").add(user);
//}
}