package com.example.eventsigninapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import kotlin.Unit;

public class EventCreationFragment extends Fragment implements EventCreationView.ConfirmButtonListener, EventCreationView.ImageButtonListener {
    private EventCreationView eventCreationView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private DatePickerDialog datePickerDialog;

    public EventCreationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventCreationView = new EventCreationView(inflater, container, getContext());
        eventCreationView.setImageButtonListener(this);
        eventCreationView.setConfirmButtonListener(this);

        createImagePickerLauncher();

        // Set up OnClickListener for dateButton
        eventCreationView.setDateButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        return eventCreationView.getRootView();
    }

    private void createImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent resultIntent = result.getData();
            if (resultCode == Activity.RESULT_OK) {
                assert resultIntent != null;
                Uri uri = resultIntent.getData();
                eventCreationView.setCaptureImage(uri);
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(resultIntent), Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), "Image picker was cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(requireContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String chosenDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                            eventCreationView.setDate(chosenDate);
                        }
                    }, year, month, day);
        }
        datePickerDialog.show();
    }

    @Override
    public void onImageButtonClick() {
        ImagePicker.with(this)
            .crop()
            .maxResultSize(1080, 1080)
            .createIntent(intent -> {
                imagePickerLauncher.launch(intent);
                return Unit.INSTANCE;
            });
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

            DatabaseController databaseController = new DatabaseController();
            databaseController.pushEventToFirestore(event);

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
}
