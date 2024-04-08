package com.example.eventsigninapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class EventCreationView {
    private final EditText eventEditText;
    private final EditText eventDescription;
    private final Button imageButton;
    private final Button confirmButton;
    private final ImageView captureImage;
    private final View rootView;
    private Uri posterUri;
    private final TextView dateButton;
    private final Context context;
    private DatePickerDialog datePickerDialog;
    private View.OnClickListener dateButtonClickListener;

    public EventCreationView(LayoutInflater inflater, ViewGroup parent, Context context) {
        rootView = inflater.inflate(R.layout.fragment_event, parent, false);
        this.context = context;

        eventEditText = rootView.findViewById(R.id.titleOfEvent);
        eventDescription = rootView.findViewById(R.id.descriptionOfEvent);
        imageButton = rootView.findViewById(R.id.imageButton);
        captureImage = rootView.findViewById(R.id.imageView);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        dateButton = rootView.findViewById(R.id.datePickerButton);

        // Set up OnClickListener for dateButton
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateButtonClickListener != null) {
                    dateButtonClickListener.onClick(v);
                }
            }
        });
    }

    public void setDateButtonClickListener(View.OnClickListener listener) {
        dateButton.setOnClickListener(listener);
    }

    public void setDate(String date) {
        dateButton.setText(date);
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

    interface ImageButtonListener {
        void onImageButtonClick();
    }

    interface ConfirmButtonListener {
        void onConfirmButtonClick();
    }
}
