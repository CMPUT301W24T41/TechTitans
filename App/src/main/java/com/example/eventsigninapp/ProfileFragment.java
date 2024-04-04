package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * A fragment that displays the user profile information and allows for editing.
 * Implements {@link EditProfileFragment.OnProfileUpdateListener} to handle profile updates.
 */
public class ProfileFragment extends Fragment implements EditProfileFragment.OnProfileUpdateListener{


    UserController userController = new UserController();
    DatabaseController databaseController = new DatabaseController();


    Button editButton;
    TextView firstName;
    TextView lastName;
    TextView contact;
    ImageView profPic;

    Uri profilePictureUri = userController.getUser().getPicture();

    /**
     * Default constructor for the ProfileFragment.
     * Constructs a new instance of the ProfileFragment.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        editButton = rootView.findViewById(R.id.editButton);
        firstName = rootView.findViewById(R.id.user_first_name);
        lastName = rootView.findViewById(R.id.user_last_name);
        contact = rootView.findViewById(R.id.user_number);
        profPic = rootView.findViewById(R.id.profilePicture);

        firstName.setText(userController.getUser().getFirstName());
        lastName.setText(userController.getUser().getLastName());
        contact.setText(userController.getUser().getContact());


        //profPic.setImageDrawable(initialsDrawable);
        updateProfilePicture(profilePictureUri);

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserController.selectImage(ProfileFragment.this);
            }
        });

        // this supposed to open a fragment to edit the user info
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setOnProfileUpdateListener(ProfileFragment.this);
                editProfileFragment.show(getChildFragmentManager(), "profileEditDialog");

            }
        });

        // this supposed to delete your profile image
        Button deletePictureButton = rootView.findViewById(R.id.deleteButton);
        deletePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the deleteProfilePicture() method of your UserController
                userController.deleteProfilePicture(getContext());
                databaseController.deleteProfilePicture(userController.getUser());
                // update your UI to reflect the deletion of the picture
                updateProfilePicture(null);
            }
        });

        Button adminControlsButton = rootView.findViewById(R.id.adminButton);
        adminControlsButton.setVisibility(rootView.INVISIBLE);

        if(userController.userIsAdmin() != null) {
            if (userController.userIsAdmin()) {
                adminControlsButton.setVisibility(rootView.VISIBLE);
            }
        }
        adminControlsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AdminActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            databaseController.uploadProfilePicture(imageUri, userController.getUser());
            databaseController.putUserToFirestore(userController.getUser());
            // Update profilePictureUrl with the new URI
            updateProfilePicture(imageUri);

        }
    }


    @Override
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newPicture) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        contact.setText(newContact);

        updateProfilePicture(newPicture);
        // user has been updated now we going to put it in the DB
        databaseController.putUserToFirestore(userController.getUser());

    }

    /**
     * Updates the profile picture displayed in the ImageView using the provided URI.
     *
     * @param newPicture The URI of the new profile picture. If null, a placeholder image will be used.
     */
    private void updateProfilePicture(Uri newPicture) {
        // Update profile picture
        Picasso picasso = Picasso.get();
        Uri pictureUri;
        String userInitials = userController.getUser().getInitials();
        Drawable initialsDrawable = InitialsDrawableGenerator.generateInitialsDrawable(userInitials);

        if (newPicture != null) {
            pictureUri = newPicture;
        } else {
            pictureUri = Uri.EMPTY;
        }

        // Load the pictureUri using Picasso
        picasso.load(pictureUri)
                .placeholder(initialsDrawable)
                .error(initialsDrawable)
                .into(profPic);
    }
}