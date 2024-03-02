package com.example.eventsigninapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 */
public class EditProfileFragment extends DialogFragment {
    private EditText firstNameEditText, lastNameEditText, contactEditText;
    UserIdController userIdController;

    public EditProfileFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UserIdController (you may want to pass it as an argument to the fragment)
        userIdController = new UserIdController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Find views
        firstNameEditText = view.findViewById(R.id.editTextFirstName);
        lastNameEditText = view.findViewById(R.id.editTextLastName);
        contactEditText = view.findViewById(R.id.editTextContact);
        Button saveButton = view.findViewById(R.id.buttonSave);

        // Set click listener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstNameEditText.getText().toString();
                String newLastName = lastNameEditText.getText().toString();
                String newContact = contactEditText.getText().toString();

                userIdController.editProfile(newFirstName, newLastName, newContact, userIdController.getUser().getPicture());

                dismiss();

            }
        });

        return view;
    }

}