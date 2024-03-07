package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AttendeeListActivity extends AppCompatActivity {
    TextView eventTitle;
    ListView checkedInList;
    ListView signedUpList;
    Button switchToMapButton;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        eventTitle = findViewById(R.id.event_title_text);
        checkedInList = findViewById(R.id.checked_in_list);
        signedUpList = findViewById(R.id.signed_up_list);
        switchToMapButton = findViewById(R.id.button_to_map_view);
        backButton = findViewById(R.id.back_button);

        switchToMapButton.setOnClickListener(listener -> {
            Intent startMapActivity = new Intent(AttendeeListActivity.this, MapActivity.class);
            startActivity(startMapActivity);
        });
    }
}
