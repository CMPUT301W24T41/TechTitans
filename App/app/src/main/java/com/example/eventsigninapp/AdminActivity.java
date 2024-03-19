package com.example.eventsigninapp;

//import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.messaging.FirebaseMessaging;




public class AdminActivity extends AppCompatActivity{

    FrameLayout frameLayout;

    TabLayout tabLayout;

    //    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseController databaseController = new DatabaseController();
    UserController userController = new UserController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        frameLayout = findViewById(R.id.adminMain);
        tabLayout = findViewById(R.id.adminTabs);



        // Set the selected tab to the second tab (index 1, which is the "Home" tab)
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab != null) {
            tab.select();
        }

        //TODO
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

                    //TODO
                    case 0:
                        fragment = new EventCreationFragment();
                        break;
                    case 1:
                        fragment = new HomeFragment();
                        break;
                    case 2:
                        fragment = new CheckInFragment();
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