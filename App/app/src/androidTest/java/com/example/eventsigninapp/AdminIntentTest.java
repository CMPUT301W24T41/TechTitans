package com.example.eventsigninapp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static com.google.common.reflect.Reflection.getPackageName;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.hamcrest.CoreMatchers;
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
        User dummyUser = new User("00001", "ToBeDeletedUser", "lastName", "123-456-7890","google.ca");
        databaseController.putUserToFirestore(dummyUser);
        databaseController.pushEventToFirestore(dummyEvent);
        Uri blankImageUri = Uri.parse("android.resource://com.example." + PACKAGE_NAME + R.drawable.logo_placeholder);
        Log.d("intentTest", "setUp: " + blankImageUri);
        databaseController.uploadProfilePicture(blankImageUri, dummyUser);

    }


    @After
    public void tearDown() {
        // Release Intents after the test
        Intents.release();
    }


    @Test
    public void testDeleteImage() throws InterruptedException{
        UiDevice device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());

        // Click on "Allow" button in the system dialog
        try {
            UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
            if (allowButton.exists() && allowButton.isEnabled()) {
                allowButton.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        onView(withText("ALL IMAGES")).check(matches(isDisplayed()));
        onView(withText("ALL IMAGES")).perform(click());
        Thread.sleep(5000);


        final int[] childCount = {0};
        onView(withId(R.id.allImagesGrid)).check(matches(isDisplayed())).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (view instanceof ViewGroup) {
                    childCount[0] = ((ViewGroup) view).getChildCount();
                }
            }
        });


        Thread.sleep(5000);

        onData(anything())
                .inAdapterView(withId(R.id.allImagesGrid))
                .atPosition(0)
                .onChildView(withId(R.id.gridImageXButtons))
                .perform(click());

        Thread.sleep(5000);


        onView(withId(R.id.allImagesGrid))
                .check(matches(hasChildCount(childCount[0] - 1)));

    }


    @Test
    public void testDeleteEvent() throws InterruptedException{

        onView(withText("Events:")).check(matches(isDisplayed()));
        Thread.sleep(5000);

        onView(withId(R.id.adminList)).check(matches(hasMinimumChildCount(1)));



        // Check if "ToBeDeleteEvent" text is displayed
        onData(anything())
                .inAdapterView(withId(R.id.adminList))
                .atPosition(0) // Assert at least one matching item
                .onChildView(withText(containsString("ToBeDeletedEvent")))
                .check(matches(isDisplayed()));

        // delete event
        onData(anything())
                .inAdapterView(withId(R.id.adminList))
                .atPosition(0) // Click on the first item
                .onChildView(withId(R.id.adminViewDeleteEvent))
                .perform(click());

        Thread.sleep(5000);

        // checks if event no longer exists
        onView(allOf(withId(R.id.adminList), withText("ToBeDeletedEvent"))).check(doesNotExist());

    }




    @Test
    public void testDeleteUser() throws InterruptedException {
        dealWithNotificationPopUp();


        onView(withText("USERS")).check(matches(isDisplayed()));
        onView(withText("USERS")).perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.adminList)).check(matches(hasMinimumChildCount(1)));



        onData(anything())
                .inAdapterView(withId(R.id.adminList))
                .atPosition(0) // Assert at least one matching item
                .onChildView(withText(containsString("ToBeDeletedUser")))
                .check(matches(isDisplayed()));

        // delete event
        onData(anything())
                .inAdapterView(withId(R.id.adminList))
                .atPosition(0) // Click on the first item
                .onChildView(withId(R.id.adminViewDeleteUser))
                .perform(click());

        Thread.sleep(5000);

        // checks if event no longer exists
        onView(allOf(withId(R.id.adminList), withText("ToBeDeletedUser"))).check(doesNotExist());

    }



    @Test
    public void testGenerateCode() throws InterruptedException{
        dealWithNotificationPopUp();


        onView(withText("ADMIN")).check(matches(isDisplayed()));
        onView(withText("ADMIN")).perform(click());
        Thread.sleep(5000);


        onView(withText("No code Generated Yet")).check(matches(isDisplayed()));

        onView(withText("GENERATE")).check(matches(isDisplayed()));
        onView(withText("GENERATE")).perform(click());


        onView(withText("No code Generated Yet")).check(doesNotExist());


    }


    @Test
    public void testReturn(){
        dealWithNotificationPopUp();
        onView(withText("RETURN")).perform(click());

        // Check if the main activity is started
        intended(hasComponent(MainActivity.class.getName()));

    }

    public void dealWithNotificationPopUp(){
        UiDevice device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());

        // Click on "Allow" button in the system dialog
        try {
            UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
            if (allowButton.exists() && allowButton.isEnabled()) {
                allowButton.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }






}



