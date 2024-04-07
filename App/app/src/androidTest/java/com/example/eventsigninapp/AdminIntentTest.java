package com.example.eventsigninapp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static com.google.common.reflect.Reflection.getPackageName;
import static org.hamcrest.Matchers.greaterThan;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class AdminIntentTest {
    @Rule
    public ActivityScenarioRule<AdminActivity> scenario = new
            ActivityScenarioRule<>(AdminActivity.class);


    @Before
    public void setUp() {
        // Initialize Intents before the test
        Intents.init();
        String PACKAGE_NAME = "eventsigninapp/";

        DatabaseController databaseController = new DatabaseController();


        // making event and user that will be used to test deletion
        Event dummyEvent = new Event("00001", "ToBeDeletedEvent", "Unknown", 1000);
        User dummyUser = new User("00001", "ToBeDeletedUser", "lastName", "123-456-7890");
        databaseController.putUserToFirestore(dummyUser);
        databaseController.pushEventToFirestore(dummyEvent);
        Uri blankImageUri = Uri.parse("android.resource://com.example." + PACKAGE_NAME + R.drawable.logo_placeholder);
        Log.d("intentTest", "setUp: " + blankImageUri);
        databaseController.uploadProfilePicture(blankImageUri, dummyUser);
        databaseController.putEventPosterToFirestore("00001", blankImageUri);
        databaseController.putEventPosterToFirestore("00001_ImageGridTest", blankImageUri);



    }


    @After
    public void tearDown() {
        // Release Intents after the test
        Intents.release();
    }

    private void disableAnimations() throws IOException {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.executeShellCommand("settings put global window_animation_scale 0");
        uiDevice.executeShellCommand("settings put global transition_animation_scale 0");
        uiDevice.executeShellCommand("settings put global animator_duration_scale 0");
    }

    @Test
    public void testSomething() throws InterruptedException, IOException {
        disableAnimations();

        onView(withText("Events:")).check(matches(isDisplayed()));
        Thread.sleep(5000);

        onView(withId(R.id.adminList)).check(matches(hasMinimumChildCount(1)));


    }
}


