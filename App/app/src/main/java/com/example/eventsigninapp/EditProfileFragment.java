package com.example.eventsigninapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

/**
 */
public class EditProfileFragment extends DialogFragment {



    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newFirstName, String newLastName, String newContact);
    }

    private OnProfileUpdateListener profileUpdateListener;

    public void setOnProfileUpdateListener(OnProfileUpdateListener listener) {
        this.profileUpdateListener = listener;
    }
    private EditText firstName, lastName, contact;
    UserIdController userIdController = new UserIdController();

    public EditProfileFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UserIdController (you may want to pass it as an argument to the fragment)
        userIdController = new UserIdController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_profile_fragment, container, false);

        // Find views
        firstName = view.findViewById(R.id.editFirstName);
        lastName = view.findViewById(R.id.editLastName);
        contact = view.findViewById(R.id.editContact);
        Button saveButton = view.findViewById(R.id.buttonSave);

        firstName.setText(userIdController.getUser().getFirstName());
        lastName.setText(userIdController.getUser().getLastName());
        contact.setText(userIdController.getUser().getContact());

        // Set click listener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstName.getText().toString();
                String newLastName = lastName.getText().toString();
                String newContact = contact.getText().toString();

                if (profileUpdateListener != null) {
                    profileUpdateListener.onProfileUpdate(newFirstName, newLastName, newContact);
                }
                userIdController.editProfile(newFirstName, newLastName, newContact, userIdController.getUser().getPicture());
                //notify();
                dismiss();

            }
        });

        return view;
    }

}