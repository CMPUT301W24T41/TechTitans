package com.example.eventsigninapp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

/**
 */
public class EditProfileFragment extends DialogFragment {


     DatabaseController databaseController = new DatabaseController();


    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newPicture);
    }


    private OnProfileUpdateListener profileUpdateListener;

    public void setOnProfileUpdateListener(OnProfileUpdateListener listener) {
        this.profileUpdateListener = listener;
    }
    private EditText firstName, lastName, contact;
    private ImageView profPic;
    UserController userController = new UserController();

    Uri profilePictureUri = userController.getUser().getPicture();
    String profilePictureUrl = profilePictureUri != null ? profilePictureUri.toString() : "";

    String userInitials = userController.getUser().getInitials();
    Drawable initialsDrawable = InitialsDrawableGenerator.generateInitialsDrawable(userInitials);
    public EditProfileFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UserIdController (you may want to pass it as an argument to the fragment)
        userController = new UserController();
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


        firstName.setText(userController.getUser().getFirstName());
        lastName.setText(userController.getUser().getLastName());
        contact.setText(userController.getUser().getContact());

        // this gives you a default dummy profile pic
        // if there is no profile pic in the database
        if (!profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(profPic);
        } else {
            // Load a default image instead
            Picasso.get().load(R.drawable.user).into(profPic);
        }

        profPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UserController.selectImage(EditProfileFragment.this);
            }


        });

        // Set click listener for saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = firstName.getText().toString();
                String newLastName = lastName.getText().toString();
                String newContact = contact.getText().toString();
                Uri newProf = userController.getUser().getPicture();


                // user gets updated here
                userController.editProfile(newFirstName, newLastName, newContact, newProf);
                databaseController.putUserToFirestore(userController.getUser());
                profileUpdateListener.onProfileUpdate(newFirstName, newLastName, newContact, newProf);


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

            // for speed purposes
            userController.getUser().setPicture(imageUri);
            databaseController.uploadProfilePicture(imageUri, userController.getUser());


            if (imageUri != null) {
                Picasso.get().load(imageUri).into(profPic);
            } else {
                // Load a default image instead
                profPic.setImageDrawable(initialsDrawable);
            }
            // Update profilePictureUrl with the new image URI
            profilePictureUrl = imageUri.toString();

        }
    }


}