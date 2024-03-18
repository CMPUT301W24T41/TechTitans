package com.example.eventsigninapp;

//import static com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

// Citation
// OpenAI, 2024, ChatGPT, (guidance on implementing the toolbar,
// Check for syntax errors
//----
// Code utilizing the ImagePicker library by Dhaval Soneji.
// Library source: https://github.com/Dhaval2404/ImagePicker
//----

public class MainActivity extends AppCompatActivity{

    FrameLayout frameLayout;

    TabLayout tabLayout;
    Map<String, Object> user = new HashMap<>();
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView idText;
    UserController userController = new UserController();
    DatabaseController databaseController = new DatabaseController();

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // Inform user that your app will not show notifications.
                    Toast.makeText(MainActivity.this, "Notifications permission denied. App will not show notifications.", Toast.LENGTH_SHORT).show();
                }
            });


    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {

       FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this finds the current user and sends the result to userController
        String defaultID = userController.getUserID(this);
        databaseController.updateWithUserFromFirestore(defaultID, userController);
        Log.d("admin", "User ID: " + defaultID);
        Log.d("admin", "User is an admin " + ( userController.isAdmin()));
        //FCM Notification Permission
        askNotificationPermission();



        frameLayout = findViewById(R.id.eventButton);
        tabLayout = findViewById(R.id.mainTabLayout);
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (task.isSuccessful()) {
//                            // Token retrieval successful, log the token
//                            String token = task.getResult();
//                            Log.d(TAG, "FCM Token: " + token);
//                            userController.setFcmToken(token);
//
//                        } else {
//                            // Token retrieval failed, log the error
//                            Exception exception = task.getException();
//                            Log.e(TAG, "Error fetching FCM registration token: " + exception.getMessage());
//                        }
//                    }
//                });



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
                        fragment = new EventCreationFragment();
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