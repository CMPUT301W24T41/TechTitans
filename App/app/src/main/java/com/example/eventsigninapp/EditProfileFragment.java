package com.example.eventsigninapp;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 */
public class EditProfileFragment extends DialogFragment {


     DatabaseController databaseController = new DatabaseController();


    public interface OnProfileUpdateListener {
        void onProfileUpdate(String newFirstName, String newLastName, String newContact, String newHomepage, Uri newPicture);
    }


    private OnProfileUpdateListener profileUpdateListener;

    public void setOnProfileUpdateListener(OnProfileUpdateListener listener) {
        this.profileUpdateListener = listener;
    }
    private EditText firstName, lastName, contact, homepage;
    private ImageView profPic;
    UserController userController = new UserController();

    Uri profilePictureUri = userController.getUser().getPicture();
    String profilePictureUrl = profilePictureUri != null ? profilePictureUri.toString() : "";

    String userInitials;
    Drawable initialsDrawable;
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
        userInitials = userController.getUser().getInitials();
        initialsDrawable = InitialsDrawableGenerator.generateInitialsDrawable(userInitials);
        // Find views
        firstName = view.findViewById(R.id.editFirstName);
        lastName = view.findViewById(R.id.editLastName);
        contact = view.findViewById(R.id.editContact);
        profPic = view.findViewById(R.id.editProfileImage);
        homepage = view.findViewById(R.id.editURL);
        Button saveButton = view.findViewById(R.id.buttonSave);


        firstName.setText(userController.getUser().getFirstName());
        lastName.setText(userController.getUser().getLastName());
        contact.setText(userController.getUser().getContact());
        homepage.setText(userController.getUser().getHomePageUrl());

        // this gives you a default dummy profile pic


        // if there is no profile pic in the database
        if (userController.getUser().isProfileSet()) {
            Picasso.get().load(profilePictureUrl).into(profPic);
        } else {
            // Load a default image instead
            profPic.setImageDrawable(initialsDrawable);
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
                String newHomepage = homepage.getText().toString();
                Uri newProf = userController.getUser().getPicture();


                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber num = phoneUtil.parse(newContact, "CA");
                } catch (NumberParseException e) {
                    contact.setError("Enter a Canadian Number");
                    return;
                }

                //setting an https before hand for validation
                if (!newHomepage.startsWith("http://") && !newHomepage.startsWith("https://")) {
                    newHomepage = "http://" + newHomepage;
                }
                try {
                    new URL(newHomepage).toURI();
                } catch (URISyntaxException | MalformedURLException e) {
                    homepage.setError("Invalid URL");
                    return;
                }

                // user gets updated here
                userController.editProfile(newFirstName, newLastName, newContact, newHomepage, newProf);
                databaseController.putUserToFirestore(userController.getUser());
                profileUpdateListener.onProfileUpdate(newFirstName, newLastName, newContact, newHomepage, newProf);


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
                userController.getUser().setProfileSet(true);
            } else {
                // Load a default image instead
                profPic.setImageDrawable(initialsDrawable);
            }
//            // Update profilePictureUrl with the new image URI
//            profilePictureUrl = imageUri.toString();

        }
    }


}