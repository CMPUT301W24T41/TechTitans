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
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ProfileFragment extends Fragment implements EditProfileFragment.OnProfileUpdateListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    UserController userController = new UserController();
    DatabaseController databaseController = new DatabaseController();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button editButton;
    TextView firstName;
    TextView lastName;
    TextView contact;
    ImageView profPic;

    Uri profilePictureUri = userController.getUser().getPicture();
    String profilePictureUrl = profilePictureUri != null ? profilePictureUri.toString() : "";

    //This will deal with the profile picture/initials
    String userInitials = userController.getUser().getInitials();
    Drawable initialsDrawable = InitialsDrawableGenerator.generateInitialsDrawable(userInitials);

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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


        profPic.setImageDrawable(initialsDrawable);

        if (!profilePictureUrl.isEmpty()) {
            // Load the profile picture
            Picasso.get().load(profilePictureUrl).into(profPic, new Callback() {
                @Override
                public void onSuccess() {
                    // Do nothing, image loaded successfully
                }
                @Override
                public void onError(Exception e) {
                    // If there's an error loading the image, fallback to displaying the initials drawable
                    profPic.setImageDrawable(initialsDrawable);
                }
            });
        } else {
            // If no profile picture URL is available, set the initials drawable directly
            profPic.setImageDrawable(initialsDrawable);
        }

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
                profilePictureUrl = "";
                // update your UI to reflect the deletion of the picture
                profPic.setImageDrawable(initialsDrawable);
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
            Uri profilePictureUri = userController.getUser().getPicture();
            String profilePictureUrl = profilePictureUri != null ? profilePictureUri.toString() : "";
            //Picasso.get().invalidate(imageUri);
            if (!profilePictureUrl.isEmpty()) {
                Picasso.get().load(imageUri).into(profPic);
            } else {
                // Load Initials instead
                profPic.setImageDrawable(initialsDrawable);
            }
        }
    }


    @Override
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newPicture) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        contact.setText(newContact);

        // Update profilePictureUrl with the new URI
        //Picasso.get().invalidate(profilePictureUrl);

        // this gives you a default dummy profile pic
        // if there is no profile pic in the database

        Uri profilePictureUri = userController.getUser().getPicture();
        String profilePictureUrl = profilePictureUri != null ? profilePictureUri.toString() : "";

        if (!profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(profPic);
        } else {
            // Load Initials instead
            profPic.setImageDrawable(initialsDrawable);
        }

        databaseController.putUserToFirestore(userController.getUser());


    }
}