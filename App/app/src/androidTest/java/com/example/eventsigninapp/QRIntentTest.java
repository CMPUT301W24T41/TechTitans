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
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

import android.widget.DatePicker;

import androidx.core.widget.TextViewCompat;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
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

import java.util.Calendar;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class QRIntentTest {
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
    public void openQRcodeScannerTest(){

        dealWithNotificationPopUp();
        onView(withText("Check In")).perform(click());


        onView(withId(R.id.scanButton)).perform(click());
        intended(hasAction("com.google.zxing.client.android.SCAN"));


    }

    @Test
    public void openQRShareIntentTest() throws InterruptedException {
        dealWithNotificationPopUp();
        onView(withText("Create  Event")).perform(click());

        onView(withId(R.id.createEventTitleEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.createEventDescEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.createEventCapacityEditText)).check(matches(isDisplayed()));

        onView(withId(R.id.createEventTitleEditText)).perform(typeText("TestEvent"));
        onView(withId(R.id.createEventDescEditText)).perform(typeText("Testdescript"));

        //keyboard keeps getting in the way, sleeping to deal with it
        closeSoftKeyboard();
        Thread.sleep(2000);



        onView(withId(R.id.createEventCapacityEditText)).perform(typeText("1000"));


        //keyboard keeps getting in the way, sleeping to deal with it
        closeSoftKeyboard();
        Thread.sleep(5000);

        onView(withId(R.id.datePicker)).perform(click());


        onView(withClassName(equalTo(DatePicker.class.getName()))).check(matches(isDisplayed()));
        // Get the current date
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Set the chosen date (e.g., one day ahead of the current date)

        // Perform actions on the date picker dialog
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(currentYear, currentMonth, currentDay), click());



        onView(withId(android.R.id.button1)).perform(click());
        Thread.sleep(3000);

        // Verify that the chosen date is displayed in the eventCreationView
        onView(withId(R.id.datePicker))
                .check(matches(isDisplayed()))
                .check(matches(withText(equalTo((currentMonth) + "-" + currentDay + "-" + currentYear))));

        onView(withId(R.id.createEventConfirmButton)).perform(click());

        Thread.sleep(3000);

        onView(withText("Testdescript")).check((matches(isDisplayed())));
        onView(withText("CHECK-IN QR CODE")).perform(click());
        Thread.sleep(3000);


        onView(withText("Share")).perform(click());
        Thread.sleep(3000);


        intended(hasAction(android.content.Intent.ACTION_CHOOSER));
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


