package com.example.eventsigninapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



public class NotifyUsersBottomSheetFragment extends BottomSheetDialogFragment {

    EditText message, title;
    Spinner spinnerOptions;
    Button sendButton;
    String eventId;
    private Event event;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "efe084289fa29e1e8be7b013a78295d7f8b0e711";
    final private String contentType = "application/json";



    static NotifyUsersBottomSheetFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        EventController eventController = new EventController(event);

        NotifyUsersBottomSheetFragment notifyUsersBottomSheetFragment = new NotifyUsersBottomSheetFragment();
        notifyUsersBottomSheetFragment.setArguments(args);
        return notifyUsersBottomSheetFragment;

    }

    public NotifyUsersBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the event from the bundle passed from the EventListFragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("event");
            eventId = event.getUuid();
            Log.d("NotifyUsersBottomSheetFragment", "Event ID: " + eventId);
            if (event == null) {
                // Handle the case where event is null
                Log.e("EventDetailsFragment", "Event is null.");
            }
        } else {
            // Handle the case where bundle is null
            Log.e("EventDetailsFragment", "Bundle is null.");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notify_users_bottom_sheet, container, false);

        // Initialize views
        sendButton = view.findViewById(R.id.sendButton);
        message = view.findViewById(R.id.editTextMessage);
        title = view.findViewById(R.id.editTextTitle);
        spinnerOptions = view.findViewById(R.id.spinnerFilter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message.getText().toString();
                String titleText = title.getText().toString();
                String filter = spinnerOptions.getSelectedItem().toString();
                if (filter.equals("Important Update")) {
                    filter = "Important";
                }
                String topic = eventId + "-" + filter;
                Log.d("NotifyUsersBottomSheetFragment", "Topic: " + topic);
                sendNotification(titleText, messageText, topic);

                // Close the bottom sheet dialog
                dismiss();
            }

        });
        // Return the inflated view
        return view;

    }

    private void sendNotification(String title, String message, String topic) {
        //URL to send the request to "https://notif-8fzv.onrender.com/send"
        String messageId = eventId + "-" + Calendar.getInstance().getTime();
        DatabaseController databaseController = new DatabaseController();
        databaseController.putNotificationToFirestore(title,message,topic,messageId);



    }
}



