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
        // get to profile
        onView(withText("Profile")).perform(click());


        onView(withText("First Name")).check(matches(isDisplayed()));
        onView(withText("Last Name")).check(matches(isDisplayed()));
        onView(withText("Contact")).check(matches(isDisplayed()));


        //verify editing can be opened
        onView(withText("Edit")).perform(click());

        onView(withId(R.id.editFirstName)).check(matches(isDisplayed()));
        onView(withId(R.id.editLastName)).check(matches(isDisplayed()));
        onView(withId(R.id.editContact)).check(matches(isDisplayed()));
        onView(withId(R.id.editProfileImage)).check(matches(isDisplayed()));

        onView(withId(R.id.editFirstName)).perform(clearText());
        onView(withId(R.id.editLastName)).perform(clearText());
        onView(withId(R.id.editContact)).perform(clearText());

        onView(withId(R.id.editFirstName)).perform(typeText("John"));
        onView(withId(R.id.editLastName)).perform(typeText("Smith"));
        onView(withId(R.id.editContact)).perform(typeText("johnsmith@mail.com"));


        //sleeping to avoid crash
        Thread.sleep(5000);
        //verify edits went through
        onView(withId(R.id.buttonSave)).perform(click());

        onView(withText("John")).check(matches(isDisplayed()));
        onView(withText("Smith")).check(matches(isDisplayed()));
        onView(withText("johnsmith@mail.com")).check(matches(isDisplayed()));


    }


    @Test
    public void doubleProfEditTest() throws InterruptedException {
        testProfEdit();

        onView(withId(R.id.editButton)).perform(click());


        onView(withId(R.id.editFirstName)).perform(clearText());

        onView(withId(R.id.editFirstName)).perform(typeText("JoJo"));

        Thread.sleep(2000);
        onView(withId(R.id.buttonSave)).perform(click());

        onView(withText("JoJo")).check(matches(isDisplayed()));


    }


}


