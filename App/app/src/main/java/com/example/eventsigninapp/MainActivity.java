package com.example.eventsigninapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    Map<String, Object> user = new HashMap<>();
    FirebaseFirestore db  = FirebaseFirestore.getInstance();;
    TextView idText;
    UserController userController = new UserController();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       FirebaseApp.initializeApp(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //testing with a test user
        User user = new User("usr", "fname", "lname", "123456789");
        userController.setUser(user);
        userController.updateWithProfPictureFromWeb();
        userController.putUserToFirestore();

        getSupportFragmentManager().beginTransaction().add(R.id.toolbarFragmentContainer, new ToolbarFragment()).commit();

        getSupportFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, new ExampleFragment()).commit();
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