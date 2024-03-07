package com.example.eventsigninapp;

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
    private final Button imageButton;
    private final Button confirmButton;
    private final ImageView captureImage;
    private final View rootView;
    private Uri imageUri;

    public EventCreationView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_event, parent, false);

        eventEditText = rootView.findViewById(R.id.titleOfEvent);
        eventDescription = rootView.findViewById(R.id.descriptionOfEvent);
        imageButton = rootView.findViewById(R.id.imageButton);
        captureImage = rootView.findViewById(R.id.imageView);
        confirmButton = rootView.findViewById(R.id.confirmButton);
    }

    public void setImageButtonListener(ImageButtonListener listener) {
        imageButton.setOnClickListener(v -> listener.onImageButtonClick());
    }

    public void setConfirmButtonListener(ConfirmButtonListener listener) {
        confirmButton.setOnClickListener(v -> listener.onConfirmButtonClick());
    }

    public void setCaptureImage(Uri uri) {
        imageUri = uri;
        captureImage.setImageURI(uri);
    }

    public Uri getImageUri() {
        return imageUri;
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

    interface ImageButtonListener {
        void onImageButtonClick();
    }

    interface ConfirmButtonListener {
        void onConfirmButtonClick();
    }
}
