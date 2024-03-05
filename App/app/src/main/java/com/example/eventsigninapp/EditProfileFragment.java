package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 */
public class EditProfileFragment extends DialogFragment {



    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newProfilePicture);
    }


    private OnProfileUpdateListener profileUpdateListener;

    public void setOnProfileUpdateListener(OnProfileUpdateListener listener) {
        this.profileUpdateListener = listener;
    }
    private EditText firstName, lastName, contact;
    private ImageView profPic;
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
        profPic = view.findViewById(R.id.editProfileImage);
        Button saveButton = view.findViewById(R.id.buttonSave);


        firstName.setText(userIdController.getUser().getFirstName());
        lastName.setText(userIdController.getUser().getLastName());
        contact.setText(userIdController.getUser().getContact());
        profPic.setImageURI(userIdController.getUser().getPicture());

        profPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UserIdController.selectImage(EditProfileFragment.this);
            }


        });

        // Set click listener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstName.getText().toString();
                String newLastName = lastName.getText().toString();
                String newContact = contact.getText().toString();
                Uri newProf = userIdController.getUser().getPicture();
                ;

                if (profileUpdateListener != null) {
                    profileUpdateListener.onProfileUpdate(newFirstName, newLastName, newContact, newProf);
                }
                userIdController.editProfile(newFirstName, newLastName, newContact, userIdController.getUser().getPicture());
                //notify();
                dismiss();

            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();

            userIdController.getUser().setPicture(imageUri);
            profPic.setImageURI(imageUri);


        }
    }


}