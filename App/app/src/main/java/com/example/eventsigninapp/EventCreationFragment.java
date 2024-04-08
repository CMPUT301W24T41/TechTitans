package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.ByteArrayOutputStream;

import kotlin.Unit;

/**
 * A simple {@link Fragment} subclass for managing event creation.
 * Allows users to input event details, select images, and generate QR codes for events.
 */
public class EventCreationFragment extends Fragment implements EventCreationView.EventCreationListener, EventCreationView.PickLocationListener, LocationPickerDialog.DialogCloseListener {
    private EventCreationView eventCreationView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private GeoPoint eventLocation;
    private String eventDetailsQrCodeString;
    private String eventCheckInQrCodeString;
    private ActivityResultLauncher<ScanOptions> setDetailsLauncher;
    private ActivityResultLauncher<ScanOptions> setCheckInLauncher;

    /**
     * Required empty public constructor for the fragment.
     */
    public EventCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view for the fragment.
     * Initializes UI components and sets listeners for image selection and event confirmation.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventCreationView = new EventCreationView(inflater, container);
        eventCreationView.setListener(this);

        setDetailsLauncher = registerForActivityResult(new ScanContract(), this::processDetailsResult);
        setCheckInLauncher = registerForActivityResult(new ScanContract(), this::processCheckInResult);
        eventCreationView.setPickLocationListener(this);

        createImagePickerLauncher();

        return eventCreationView.getRootView();
    }

    /**
     * Creates the image picker launcher for selecting images for event posters.
     */

    private void createImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent resultIntent = result.getData();
            if (resultCode == Activity.RESULT_OK) {
                assert resultIntent != null;
                Uri uri = resultIntent.getData();
                eventCreationView.setEventPoster(uri);
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(resultIntent), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Image picker was cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Processes the result of the QR code scan for event details.
     * @param result the result of the QR code scan
     */
    private void processDetailsResult(ScanIntentResult result) {
        if (result != null) {
            eventDetailsQrCodeString = result.getContents();
        }
    }

    /**
     * Processes the result of the QR code scan for event check-in.
     * @param result the result of the QR code scan
     */
    private void processCheckInResult(ScanIntentResult result) {
        if (result != null) {
            eventCheckInQrCodeString = result.getContents();
        }
    }

    @Override
    public void onConfirmButtonClick() {
        Event event = new Event();

        String eventNameText = eventCreationView.getEventName();
        String eventDescriptionText = eventCreationView.getEventDescription();
        // Subscribe to topics ie create the 3 types of topic for this event
        subscribeToTopic(event.getUuid()+"-Important");
        subscribeToTopic(event.getUuid()+"-Reminders");
        subscribeToTopic(event.getUuid()+"-Promotions");


        if (!eventNameText.isEmpty() && !eventDescriptionText.isEmpty()) {
            event.setName(eventNameText);
            event.setDescription(eventDescriptionText);
            event.setPosterUri(eventCreationView.getPosterUri());
            event.setLocation(eventLocation);

            Bitmap bitmap = Organizer.generateQRCode(event.getEventCheckInQrCodeString());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "QR Code", null);
            event.setCheckInQRCodeUri(Uri.parse(path));

            Bitmap eventDetailsBitmap = Organizer.generateQRCode(event.getEventDetailsQrCodeString());
            eventDetailsBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String eventDetailsPath = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), eventDetailsBitmap, "Event Details QR Code", null);
            event.setDetailsQRCodeUri(Uri.parse(eventDetailsPath));

            UserController userController = new UserController();
            event.setCreatorUUID(userController.getUserID(requireContext()));

            DatabaseController DatabaseController = new DatabaseController();
            DatabaseController.pushEventToFirestore(event);

            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
            eventDetailsFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, eventDetailsFragment)
                .addToBackStack(null)
                .commit();
        } else {
            Toast.makeText(getActivity(), "Please fill up both fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onImageClick() {
        ImagePicker.with(this)
                .crop()
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return Unit.INSTANCE;
                });
    }

    @Override
    public void onSetCheckInClick() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan the QR code for event check-in");
        options.setCaptureActivity(com.example.eventsigninapp.CaptureAct.class);
        setCheckInLauncher.launch(options);
    }

    @Override
    public void onSetDetailsClick() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan the QR code for event details");
        options.setCaptureActivity(com.example.eventsigninapp.CaptureAct.class);
        setDetailsLauncher.launch(options);
    }

    /**
     * Subscribes to a topic for the event.
     * @param topic the topic to subscribe to
     */
    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener(task -> {
                String msg = "Subscribed to " + topic;
                if (!task.isSuccessful()) {
                    msg = "Failed to subscribe to " + topic;
                }
                Log.d("FCM", msg);
            });
    }


    @Override
    public void onPickLocationClick() {
        Bundle bundle = new Bundle();
        String locationQuery = eventCreationView.getLocationQuery();
        bundle.putString("query", locationQuery);
        LocationPickerDialog pickLocation = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            pickLocation = new LocationPickerDialog(this);
            pickLocation.setArguments(bundle);
            pickLocation.show(getActivity().getSupportFragmentManager(), "Select location");
        } else {
            Toast.makeText(requireContext(), "Make sure that the API used is current", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogClose(Location location) {
        if (location == null) {
            Toast.makeText(requireContext(), "No event location added", Toast.LENGTH_LONG).show();
            eventCreationView.clearLocation();
            return;
        }
        eventLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        Toast.makeText(requireContext(), "Event location confirmed!", Toast.LENGTH_LONG).show();
        Log.e("LOCATION", String.valueOf(location));
    }
}
