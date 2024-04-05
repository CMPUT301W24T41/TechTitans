package com.example.eventsigninapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Date;

public class EventCreationView {
    private final EditText eventTitle;
    private final EditText eventDescription;
    private final EditText eventCapacity;
    private final EditText eventDate;
    private final EditText eventTime;
    private final ImageView eventPoster;
    private final Button confirmButton;
    private final Button setCheckInButton;
    private final Button setDetailsButton;
    private final View rootView;
    private Uri posterUri;

    public EventCreationView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_create_event, parent, false);

        eventTitle = rootView.findViewById(R.id.createEventTitleEditText);
        eventDescription = rootView.findViewById(R.id.createEventDescEditText);
        eventPoster = rootView.findViewById(R.id.createEventImageView);
        eventCapacity = rootView.findViewById(R.id.createEventCapacityEditText);
        eventDate = rootView.findViewById(R.id.createEventDateEditText);
        eventTime = rootView.findViewById(R.id.createEventTimeEditText);
        //captureImage = rootView.findViewById(R.id.imageView);
        setCheckInButton = rootView.findViewById(R.id.createEventSetCheckInButton);
        setDetailsButton = rootView.findViewById(R.id.createEventSetDetailsButton);
        confirmButton = rootView.findViewById(R.id.createEventConfirmButton);
    }

    public void setListener(EventCreationListener listener) {
        eventPoster.setOnClickListener(v -> listener.onImageClick());
        confirmButton.setOnClickListener(v -> listener.onConfirmButtonClick());
        setCheckInButton.setOnClickListener(v -> listener.onSetCheckInClick());
        setDetailsButton.setOnClickListener(v -> listener.onSetDetailsClick());
    }

    public void setEventPoster(Uri uri) {
        posterUri = uri;
        eventPoster.setImageURI(uri);
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public View getRootView() {
        return rootView;
    }

    public String getEventName() {
        return eventTitle.getText().toString().trim();
    }

    public String getEventDescription() {
        return eventDescription.getText().toString().trim();
    }

    public Integer getEventCapacity() {
        return Integer.parseInt(eventCapacity.getText().toString().trim());
    }

    interface EventCreationListener {
        void onConfirmButtonClick();
        void onImageClick();
        void onSetCheckInClick();
        void onSetDetailsClick();
    }
}
