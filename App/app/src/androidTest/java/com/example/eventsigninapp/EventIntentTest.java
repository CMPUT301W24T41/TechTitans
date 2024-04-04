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
public class EventIntentTest {
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
    public void testCreateAndViewEvent() throws InterruptedException {
        onView(withText("Create  Event")).perform(click());

        onView(withId(R.id.titleOfEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionOfEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.attendeeLimit)).check(matches(isDisplayed()));

        onView(withId(R.id.titleOfEvent)).perform(typeText("TestEvent"));
        onView(withId(R.id.descriptionOfEvent)).perform(typeText("Testdescript"));
        onView(withId(R.id.attendeeLimit)).perform(typeText("1000"));


        closeSoftKeyboard();
        Thread.sleep(2000);
        onView(withId(R.id.confirmButton)).perform(click());


        onView(withId(R.id.qrCodeImageView)).check((matches(isDisplayed())));

        onView(withText("Cancel")).perform(click());

        onView(withText("Home")).perform(click());

        onView(withText("TestEvent"));





    }

}


