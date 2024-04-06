package com.example.eventsigninapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EventCreationView {
    private final EditText eventEditText;
    private final EditText eventDescription;
    private final Button locationPickerButton;
    private final EditText locationPickerText;
    private final Button imageButton;
    private final Button confirmButton;
    private final ImageView captureImage;
    private final View rootView;
    private Uri posterUri;

    public EventCreationView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_event, parent, false);

        eventEditText = rootView.findViewById(R.id.titleOfEvent);
        eventDescription = rootView.findViewById(R.id.descriptionOfEvent);
        imageButton = rootView.findViewById(R.id.imageButton);
        captureImage = rootView.findViewById(R.id.imageView);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        locationPickerButton = rootView.findViewById(R.id.locationPickerButton);
        locationPickerText = rootView.findViewById(R.id.locationPickerText);
    }

    public void setPickLocationListener(PickLocationListener listener) {
        locationPickerButton.setOnClickListener(v -> listener.onPickLocationClick());
    }

    public void setImageButtonListener(ImageButtonListener listener) {
        imageButton.setOnClickListener(v -> listener.onImageButtonClick());
    }

    public void setConfirmButtonListener(ConfirmButtonListener listener) {
        confirmButton.setOnClickListener(v -> listener.onConfirmButtonClick());
    }

    public void setCaptureImage(Uri uri) {
        posterUri = uri;
        captureImage.setImageURI(uri);
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public View getRootView() {
        return rootView;
    }

    public String getEventName() {
        return eventEditText.getText().toString().trim();
    }

    public String getEventDescription() {
        return eventDescription.getText().toString().trim();
    }

    public String getLocationQuery() {
        return locationPickerText.getText().toString().trim();
    }

    public void clearLocation() {
        locationPickerText.setText("");
    }

    interface ImageButtonListener {
        void onImageButtonClick();
    }

    interface ConfirmButtonListener {
        void onConfirmButtonClick();
    }

    interface PickLocationListener {
        void onPickLocationClick();
    }
}
