package com.example.eventsigninapp;

import static android.media.MediaSyncEvent.createEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    FrameLayout frameLayout;

    TabLayout tabLayout;
    Map<String, Object> user = new HashMap<>();
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView idText;
    UserController userController = new UserController();
    DatabaseController databaseController = new DatabaseController();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // this finds the current user and sends the result to userController
        String defaultID = userController.getUserID(this);
        databaseController.updateWithUserFromFirestore(defaultID, userController);


        frameLayout = findViewById(R.id.eventButton);
        tabLayout = findViewById(R.id.mainTabLayout);


        // Set the selected tab to the second tab (index 1, which is the "Home" tab)
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null) {
            tab.select();
        }

        // Set the default selected tab to HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .addToBackStack(null)
                .commit();

        // Set listener for tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new EventFragment();
                        break;
                    case 1:
                        fragment = new HomeFragment();
                        break;
                    case 2:
                        fragment = new CheckInFragment();
                        break;
                    case 3:
                        fragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        /* Testing Database
        userController = new UserIdController();
        // testing on creating an event and saving it to firebase

        String eventName = "Event Name"; // Example: Retrieve the event name from EditText
        String eventLocation = "Chicago"; // Example: Retrieve the event location from EditText
        String userId = userController.getUserID(this);
        Event event = new Event();

        // Set other event properties as needed
        event.setName(eventName);
        event.setLocation(eventLocation);

        //testing with a test user
        //User user = new User("usr", "fname", "lname", "123456789");
        //userController.setUser(user);
        userController.getUserFromFirestore("usr");
        //userController.putUserToFirestore();


        event.createEvent(userId, eventName);
        System.out.println("DONE");

         */

    }

}