package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AttendeeListActivity extends AppCompatActivity {
    TextView eventTitle;
    ListView checkedInListView;
    ListView signedUpListView;
    Button switchToMapButton;
    Button backButton;
    ArrayList<User> signedUpUsers;
    ArrayList<User> checkedInUsers;
    ArrayAdapter<User> signedUpUserAdapter;
    ArrayAdapter<User> checkedInUserAdapter;
    AttendeeListController alController;
    DatabaseController dbController;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_list);

        // for testing, no event is passed yet
        alController = new AttendeeListController();
        // TODO: Uncomment
        // alController = new AttendeeListController(event);
        dbController = new DatabaseController();
        eventTitle = findViewById(R.id.event_title_text);
        checkedInListView = findViewById(R.id.checked_in_list);
        signedUpListView = findViewById(R.id.signed_up_list);
        switchToMapButton = findViewById(R.id.button_to_map_view);
        backButton = findViewById(R.id.back_button);

        signedUpUsers = alController.getSignedUpUsers();
        checkedInUsers = alController.getCheckedInUsers();

        signedUpUserAdapter = new UserArrayAdapter(this, signedUpUsers);
        checkedInUserAdapter = new UserArrayAdapter(this, checkedInUsers);

        signedUpListView.setAdapter(signedUpUserAdapter);
        checkedInListView.setAdapter(checkedInUserAdapter);

        dbController.getSignedUpUsersFromFirestore(event.getUuid(), alController);
        dbController.getCheckedInUsersFromFirestore(event.getUuid(), alController);

        switchToMapButton.setOnClickListener(listener -> {
            Intent startMapActivity = new Intent(AttendeeListActivity.this, MapActivity.class);
            startActivity(startMapActivity);
        });

        signedUpUserAdapter.notifyDataSetChanged();
        checkedInUserAdapter.notifyDataSetChanged();
    }
}
