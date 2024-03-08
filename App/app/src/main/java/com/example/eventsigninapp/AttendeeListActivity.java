package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttendeeListActivity extends AppCompatActivity {
    TextView eventTitle;
    ListView checkedInListView;
    ListView signedUpListView;
    Button switchToMapButton;
    Button backButton;
    Event event;
    List<String> signedUpUsers;
    List<String> checkedInUsers;
    ArrayAdapter<String> signedUpUserAdapter;
    ArrayAdapter<String> checkedInUserAdapter;
    DatabaseController dbController;
    String eventId = "131e7de5-38de-4cce-ab04-230a5f2ca76f"; // for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event.setUuid(eventId);
        setContentView(R.layout.activity_attendee_list);

        // for testing, no event is passed yet
        // TODO: Uncomment
        // alController = new AttendeeListController(event);
        dbController = new DatabaseController();
        eventTitle = findViewById(R.id.event_title_text);
        checkedInListView = findViewById(R.id.checked_in_list);
        signedUpListView = findViewById(R.id.signed_up_list);
        switchToMapButton = findViewById(R.id.button_to_map_view);
        backButton = findViewById(R.id.back_button);


        signedUpUserAdapter = new UserArrayAdapter(this, signedUpUsers);
        checkedInUserAdapter = new UserArrayAdapter(this, checkedInUsers);

        signedUpListView.setAdapter(signedUpUserAdapter);
        checkedInListView.setAdapter(checkedInUserAdapter);


        switchToMapButton.setOnClickListener(listener -> {
            Intent startMapActivity = new Intent(AttendeeListActivity.this, MapActivity.class);
            startActivity(startMapActivity);
        });

        signedUpUserAdapter.notifyDataSetChanged();
        checkedInUserAdapter.notifyDataSetChanged();
    }

}
