package com.example.eventsigninapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<String, Object> user = new HashMap<>();
    FirebaseFirestore db;
    private UserIdController userIdController;
    TextView idText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userIdController = new UserIdController();
        User user = new User("temp", "f", "l");
        userIdController.putUserToFirestore(user);

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