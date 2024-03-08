package com.example.eventsigninapp;

import static android.icu.number.NumberRangeFormatter.with;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
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
public class IntentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);



    @Test
    public void testProfAdd() throws InterruptedException {
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

}
