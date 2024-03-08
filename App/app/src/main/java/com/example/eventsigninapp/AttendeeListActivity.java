package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttendeeListActivity extends AppCompatActivity implements
        DatabaseController.GetCheckedInUsersCallback,
        DatabaseController.GetSignedUpUsersCallback {
    TextView eventTitle;
    ListView checkedInListView;
    ListView signedUpListView;
    Button switchToMapButton;
    Button backButton;
    UserArrayAdapter signedUpUserAdapter;
    UserArrayAdapter checkedInUserAdapter;
    DatabaseController dbController;
    Event event;
    ArrayList<User> signedUpUsers;
    ArrayList<User> checkedInUsers;
    String eventId = "131e7de5-38de-4cce-ab04-230a5f2ca76f"; // for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = new Event();

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

        signedUpUsers = new ArrayList<User>();
        checkedInUsers = new ArrayList<User>();

        signedUpUserAdapter = new UserArrayAdapter(this, signedUpUsers);
        checkedInUserAdapter = new UserArrayAdapter(this, checkedInUsers);

        signedUpListView.setAdapter(signedUpUserAdapter);
        checkedInListView.setAdapter(checkedInUserAdapter);

        dbController.getCheckedInUsersFromFirestore(event, this);
        dbController.getSignedUpUsersFromFirestore(event, this);

        signedUpUserAdapter.notifyDataSetChanged();
        checkedInUserAdapter.notifyDataSetChanged();

        switchToMapButton.setOnClickListener(listener -> {
            Intent startMapActivity = new Intent(AttendeeListActivity.this, MapActivity.class);
            startActivity(startMapActivity);
        });
    }

    @Override
    public void onGetSignedUpUsersCallback(Event event, ArrayList<?> users) {
        try {
            for (int i = 0; i < users.size(); i++) {
                event.addSignedUpUser((String) users.get(i));
                dbController.getUserFromFirestore((String) users.get(i), new DatabaseController.UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        signedUpUsers.add(user);
                        signedUpUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                Log.e("DEBUG", String.format("User %s signed up", (String) users.get(i)));
            }
        } catch (Exception e) {
            Log.e("DEBUG", String.format("Error: %s", e.getMessage()));
        }
    }

    @Override
    public void onGetCheckedInUsersCallback(Event event, ArrayList<?> users) {
        try {
            for (int i = 0; i < users.size(); i++) {
                event.addCheckedInUser((String) users.get(i));
                dbController.getUserFromFirestore((String) users.get(i), new DatabaseController.UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        checkedInUsers.add(user);
                        checkedInUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                Log.e("DEBUG", String.format("User %s checked in", users.get(i)));
            }
        } catch (Exception e) {
                Log.e("DEBUG", String.format("Error: %s", e.getMessage()));
            }
    }

}
