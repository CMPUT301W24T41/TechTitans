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
public class FragmentIntentTest {
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
    public void testHomeFragment() {
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

        onView(withText("Home")).perform(click());
        onView(withText("Browse All Events")).check(matches(isDisplayed()));
    }

    @Test
    public void testEventDetailsFragment() {
        onView(withText("Home")).perform(click());

        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.all_events_list
        )).atPosition(0).perform(click());

        onData(is(instanceOf(Event.class))).inAdapterView(withId(R.id.all_events_list)).atPosition(0).perform(click());

    }

    @Test
    public void testMyEventsFragment() {
        onView(withText("My Events")).perform(click());
        onView(withText("My Signed Up Events")).check(matches(isDisplayed()));
    }

}


