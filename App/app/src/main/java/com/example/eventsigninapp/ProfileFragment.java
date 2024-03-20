package com.example.eventsigninapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
        // this gives you a default dummy profile pic
        // if there is no profile pic in the database
        if (!profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(profPic);
        } else {
            // Load a default image instead
            Picasso.get().load(R.drawable.user).into(profPic);
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

                // update your UI to reflect the deletion of the picture
                profPic.setImageResource(R.drawable.user);
            }
        });

        Button adminControlsButton = rootView.findViewById(R.id.adminButton);
        if(!userController.userIsAdmin()) {
            adminControlsButton.setVisibility(rootView.INVISIBLE);
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
            //Picasso.get().invalidate(imageUri);
            Picasso.get().load(imageUri).into(profPic);
        }
    }


    @Override
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newPicture) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        contact.setText(newContact);

        // Update profilePictureUrl with the new URI
        Picasso.get().invalidate(profilePictureUrl);

        // this gives you a default dummy profile pic
        // if there is no profile pic in the database
        if (!profilePictureUrl.isEmpty()) {
            Picasso.get().load(profilePictureUrl).into(profPic);
        } else {
            // Load a default image instead
            Picasso.get().load(R.drawable.user).into(profPic);
        }

        databaseController.putUserToFirestore(userController.getUser());


    }
}