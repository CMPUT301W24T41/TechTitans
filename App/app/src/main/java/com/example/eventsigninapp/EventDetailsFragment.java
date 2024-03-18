package com.example.eventsigninapp;


import static com.google.firebase.messaging.Constants.TAG;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.eventsigninapp.DatabaseController.EventImageUriCallbacks;

import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * This class acts as a controller for the event details page.
 */
public class EventDetailsFragment extends Fragment {
    DatabaseController databaseController = new DatabaseController();
    UserController userController = new UserController();




    private TextView eventDescription, announcement;
    private ImageView eventPoster;
    private Button backButton, editEventButton, notifyUsersButton;
    private ToggleButton signUpButton;

    /**
     * Used for passing in data through Bundle from
     * Event list fragment. Data passed should be Event Class.
     */
    static EventDetailsFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        EventController eventController = new EventController(event);

        EventDetailsFragment eventFragment = new EventDetailsFragment();
        eventFragment.setArguments(args);
        return eventFragment;
    }

    /**
     *
     * Called when fragment is created.
     * Setting display details to data passed through Bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eventdetails, container, false);

        // Find views
        eventDescription = view.findViewById(R.id.eventDetails);
        announcement = view.findViewById(R.id.eventAnnouncements);
        eventPoster = view.findViewById(R.id.poster);
        editEventButton = view.findViewById(R.id.editEventButton);
        notifyUsersButton = view.findViewById(R.id.notifyUsersButton);
        backButton = view.findViewById(R.id.btnEventDetails);
        signUpButton = view.findViewById(R.id.signUpButton);

        Bundle bundle = getArguments();
        Event event = (Event) bundle.get("event");
        eventDescription.setText(event.getDescription());

        DatabaseController databaseController = new DatabaseController();
        // Call the getEventPoster method with the event UUID and implement the callback interface


        // Call the getEventPoster function
        databaseController.getEventPoster(event.getUuid(), callback);

        if(userController.getUser().getId().equals(event.getCreatorUUID())){
            editEventButton.setVisibility(View.VISIBLE);
            notifyUsersButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.GONE);

        }
        else{
            editEventButton.setVisibility(View.GONE);
            notifyUsersButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }

        notifyUsersButton.setOnClickListener(v -> {
            NotifyUsersBottomSheetFragment bottomSheetFragment = new NotifyUsersBottomSheetFragment();
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });

        backButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        if(userController.getUser().getAttendingEvents().contains(event.getUuid())){
            signUpButton.setChecked(true);
            signUpButton.setText("Signed Up");
            signUpButton.setClickable(false);
        }
        else{
            signUpButton.setChecked(false);
            signUpButton.setText("Sign Up");
            signUpButton.setOnClickListener(v -> {
                showSignUpPopup(event);
            });

        }

        return view;
    }

    private void showSignUpPopup(Event event) {

        String[] signupOptions = {"All", "Important updates only", "Reminders only", "None"};

        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.notification_preferences_dialog, null);
        Spinner spinner = dialogView.findViewById(R.id.notification_preferences_spinner);

        // Populate the spinner with data
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, signupOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle signup confirmation
                confirmSignUp(spinner.getSelectedItem().toString(), event);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();


    }

    private void confirmSignUp(String selectedItem, Event event) {

        // Sign up the user for the event
        Log.d("EventDetailsFragment", "User id: " + userController.getUser().getId());

        userController.signUp(event);
        Log.d("EventDetailsFragment", "User signed up for event: " + event.getSignedUpUsersUUIDs());
        databaseController.pushEventToFirestore(event);
        signUpButton.setChecked(true);
        signUpButton.setText("Signed Up");
        signUpButton.setClickable(false);
        subscripeToNotifications(selectedItem, event);
    }

    private void subscripeToNotifications(String selectedItem, Event event) {
        FirebaseMessaging.getInstance().subscribeToTopic(selectedItem + event.getUuid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    EventImageUriCallbacks callback = new EventImageUriCallbacks() {
        @Override
        public void onEventPosterCallback(Uri imageUri) {
            // Handle successful retrieval of the image URI (e.g., load the image into an ImageView)
            System.out.println("EventPoster Image URI retrieved: " + imageUri.toString());
            Picasso.get().load(imageUri).into(eventPoster);

        }

        @Override
        public void onEventCheckInQRCodeCallback(Uri imageUri) {
            // to be implement
        }

        @Override
        public void onEventDescriptionQRCodeCallback(Uri imageUri) {
            // to be implemented
        }

        @Override
        public void onError(Exception e) {
            // Handle failure to retrieve the image URI
            Log.e("EventPoster", "Error getting image URI", e);
        }
    };


}
