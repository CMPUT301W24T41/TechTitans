package com.example.eventsigninapp;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.core.widget.TextViewCompat;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfileIntentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);


    @Before
    public void setUp() {
        // Initialize Intents before the test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after the test
        Intents.release();
    }

    @Test
    public void testProfEdit() throws InterruptedException {

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
        // get to profile
        Thread.sleep(3000);

        onView(withText("Profile")).perform(click());


        onView(withId(R.id.user_first_name)).check(matches(isDisplayed()));
        onView(withId(R.id.user_last_name)).check(matches(isDisplayed()));
        onView(withId(R.id.user_number)).check(matches(isDisplayed()));
        onView(withId(R.id.user_home_page)).check(matches(isDisplayed()));



        //verify editing can be opened
        onView(withId(R.id.editButton)).perform(click());

        onView(withId(R.id.editFirstName)).check(matches(isDisplayed()));
        onView(withId(R.id.editLastName)).check(matches(isDisplayed()));
        onView(withId(R.id.editContact)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileImage)).check(matches(isDisplayed()));
        onView(withId(R.id.editURL)).check(matches(isDisplayed()));

        onView(withId(R.id.editFirstName)).perform(clearText());
        onView(withId(R.id.editLastName)).perform(clearText());
        onView(withId(R.id.editContact)).perform(clearText());
        onView(withId(R.id.editURL)).perform(clearText());


        onView(withId(R.id.editFirstName)).perform(typeText("John"));
        onView(withId(R.id.editLastName)).perform(typeText("Smith"));
        onView(withId(R.id.editContact)).perform(typeText("7801234567"));
        onView(withId(R.id.editURL)).perform(typeText("google.ca"));




        //sleeping to avoid crash
        Thread.sleep(5000);
        //verify edits went through
        onView(withId(R.id.buttonSave)).perform(click());

        onView(withText("John")).check(matches(isDisplayed()));
        onView(withText("Smith")).check(matches(isDisplayed()));
        onView(withText("7801234567")).check(matches(isDisplayed()));
        onView(withText("http://google.ca")).check(matches(isDisplayed()));



    }


    // Trying to edit profile twice to see if it works
    @Test
    public void doubleProfEditTest() throws InterruptedException {
        testProfEdit();

        onView(withId(R.id.editButton)).perform(click());


        onView(withId(R.id.editFirstName)).perform(clearText());

        onView(withId(R.id.editFirstName)).perform(typeText("JoJo"));

        Thread.sleep(2000);
        onView(withId(R.id.buttonSave)).perform(click());

        onView(withText("JoJo")).check(matches(isDisplayed()));
        onView(withText("Smith")).check(matches(isDisplayed()));
        onView(withText("7801234567")).check(matches(isDisplayed()));
        onView(withText("http://google.ca")).check(matches(isDisplayed()));


    }


}


