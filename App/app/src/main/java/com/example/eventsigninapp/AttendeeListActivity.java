package com.example.eventsigninapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class AttendeeListActivity extends AppCompatActivity {
    ListView checkedInList;
    ListView signedUpList;
    Button switchToMapButton;
    DatabaseController dbController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);
    }
}
