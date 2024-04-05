package com.example.eventsigninapp;

import static android.content.Intent.getIntent;

import static androidx.core.app.ActivityCompat.recreate;

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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A fragment that displays the user profile information and allows for editing.
 * Implements {@link EditProfileFragment.OnProfileUpdateListener} to handle profile updates.
 */
public class ProfileFragment extends Fragment implements EditProfileFragment.OnProfileUpdateListener{


    static int COUNTER = 0;
    UserController userController = new UserController();
    DatabaseController databaseController = new DatabaseController();


    Button editButton;
    TextView firstName;
    TextView lastName;
    TextView contact;
    TextView homepage;
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
        homepage = rootView.findViewById(R.id.user_home_page);
        profPic = rootView.findViewById(R.id.profilePicture);

        firstName.setText(userController.getUser().getFirstName());
        lastName.setText(userController.getUser().getLastName());
        contact.setText(userController.getUser().getContact());
        homepage.setText(userController.getUser().getHomePageUrl());

        //profPic.setImageDrawable(initialsDrawable);
        updateProfilePicture(profilePictureUri);

        // for admin promotion 3 1 2 1
        firstName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String partOne = firstName.getText().toString();
                String partTwo = lastName.getText().toString();

                if (COUNTER == 4 && (partOne + partTwo).length() == 32) {
                    // reconstructing adminCode based on specification
                    String reconstructedString = partOne.substring(0, 8) + "-" + partTwo.substring(0, 4) + "-" + partOne.substring(8, 12) + "-" + partTwo.substring(4,8) + "-" + partOne.substring(12);
                    databaseController.updateAdmin(reconstructedString, userController.getUser(), getContext());
//                Toast.makeText(getActivity(), ""+COUNTER, Toast.LENGTH_SHORT).show();
                } else if (COUNTER < 3) {
                    COUNTER += 1;
                }else{
                    COUNTER = 0;
                }
            }
        });

        lastName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), ""+COUNTER, Toast.LENGTH_SHORT).show();
                if (COUNTER >= 3 && COUNTER < 10000) {
                    COUNTER *= 37;
                }else{
                    COUNTER = 0;
                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), ""+COUNTER, Toast.LENGTH_SHORT).show();
                if (COUNTER >= 22) {
                    COUNTER /= 5;
                }else{
                    COUNTER = 0;
                }
            }
        });



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

        // opens the url when the homepage is clicked
        homepage.setMovementMethod(LinkMovementMethod.getInstance());


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
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact, String newHomepage, Uri newPicture) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        contact.setText(newContact);
        homepage.setText(newHomepage);

        updateProfilePicture(newPicture);

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
            Bitmap bitmap = ((BitmapDrawable) initialsDrawable).getBitmap();

            // Save Bitmap to a file
            File file = new File(getContext().getCacheDir(), "initials_image.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                pictureUri = Uri.fromFile(file);
                // upload the image, so it persits through multiple runs and on all menus
                databaseController.uploadProfilePicture(pictureUri, userController.getUser());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Load the pictureUri using Picasso
        picasso.load(pictureUri)
                .placeholder(initialsDrawable)
                .error(initialsDrawable)
                .into(profPic);
    }
}