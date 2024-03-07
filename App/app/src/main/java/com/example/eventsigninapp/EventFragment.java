package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;

 /**
 * A simple {@link Fragment} subclass for managing event creation.
 * Allows users to input event details, select images, and generate QR codes for events.
 */
public class EventFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;
    private ImageView captureImage;

    /**
     * Required empty public constructor for the fragment.
     */
    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view for the fragment.
     * Initializes UI components and sets listeners for image selection and event confirmation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        EditText eventEditText = rootView.findViewById(R.id.titleOfEvent);
        EditText eventDescription = rootView.findViewById(R.id.descriptionOfEvent);

        Button confirmButton = rootView.findViewById(R.id.confirmButton);

        // Gets the poster/image button id
        Button imageButton = rootView.findViewById(R.id.imageButton);
        captureImage = rootView.findViewById(R.id.imageView);

        // this opens the camera or the album
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file picker or launch gallery app to select an image
                ImagePicker.Companion.with(EventFragment.this)
                        .crop()
                        .maxResultSize(1080, 1080)
                        .start(PICK_IMAGE_REQUEST);
            }

        });

        // This generates qr code
        confirmButton.setOnClickListener(v -> {
            // This passes data to the CrCodeActivity
            String eventNameText = eventEditText.getText().toString().trim();
            String eventDescriptionText = eventDescription.getText().toString().trim();

            Event event = new Event();

            // Check if both fields are not empty
            if (!eventNameText.isEmpty() && !eventDescriptionText.isEmpty()) {
                // Both fields are filled up, proceed with the action

                // Set the event name and description
                event.setName(eventNameText);
                event.setDescription(eventDescriptionText);

                // Set the creator UUID
                UserController userController = new UserController();
                event.setCreatorUUID(userController.getUserID(requireContext()));

                // Clear EditText fields
                eventEditText.setText("");
                eventDescription.setText("");

                // Save data to the database
                // (You can perform any database operations here)
                EventDatabaseController eventDatabaseController = new EventDatabaseController();
                eventDatabaseController.pushEventToFirestore(event);

                // Print the UUID of the created event
                // Tag the print for logcat
                System.out.println("Event UUID: " + event.getUuid());

                // Create intent to navigate to the QrCode activity
                Bitmap bitmap = Organizer.generateQRCode(event.getUuid());
                QRCodeFragment qrCodeFragment = QRCodeFragment.newInstance(bitmap);
                qrCodeFragment.show(getParentFragmentManager(), "qr_code_fragment");
            } else {
                // If either eventNameText or eventDescriptionText is empty,
                // show a toast or alert the user to fill both fields
                Toast.makeText(getActivity(), "Please fill up both fields", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    /**
     * Handles the result of the image selection process.
     * If an image is selected, it sets the selected image to the ImageView.
     * If no image is selected, it displays a toast message.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is for the image picker request
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            // This URI represents the location of the image in the device's storage
            // You can use this URI to load the image into your ImageView
            Uri uri = data.getData();
            captureImage.setImageURI(uri);
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
