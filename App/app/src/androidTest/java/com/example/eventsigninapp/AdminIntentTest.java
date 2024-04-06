package com.example.eventsigninapp;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminIntentTest {
    @Rule
    public ActivityScenarioRule<AdminActivity> scenario = new
            ActivityScenarioRule<>(AdminActivity.class);


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
//    @Before
//    public void setUpFirebase() {
//        // Initialize Firebase app with the testing project's credentials
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//    }

    @Test
    public void testSomething() throws InterruptedException {
        Thread.sleep(5000);

    }
}
