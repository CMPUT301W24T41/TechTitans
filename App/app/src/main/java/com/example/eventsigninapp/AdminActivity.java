package com.example.eventsigninapp;

//import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import static java.security.AccessController.getContext;

import android.content.Intent;
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
//        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        frameLayout = findViewById(R.id.adminMain);
        tabLayout = findViewById(R.id.adminTabs);




        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) {
            tab.select();
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminMain, new AdminEventListFragment())
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
                        fragment = new AdminEventListFragment();
                        break;
                    case 1:
                        fragment = new AdminUserListFragment();
                        break;
                    case 2:
                        //TODO add all images fragment
//                        fragment = new ();
                        break;
                    case 3:
                        break;

                }
                if(tab.getPosition() == 3) {
                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                    startActivity(intent);

                }else{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.adminMain, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }



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