package com.example.eventsigninapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity implements EditProfileFragment.OnProfileUpdateListener{

    TextView firstName;
    TextView lastName;
    TextView contact;

    ImageView profPic;
    UserIdController userIdController = new UserIdController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        getSupportFragmentManager().beginTransaction().add(R.id.toolbarFragmentContainer, new ToolbarFragment()).commit();

        Button editButton = findViewById(R.id.editButton);
        firstName = findViewById(R.id.user_first_name);
        lastName = findViewById(R.id.user_last_name);
        contact = findViewById(R.id.user_number);
        profPic = findViewById(R.id.profilePicture);



        firstName.setText(userIdController.getUser().getFirstName());
        lastName.setText(userIdController.getUser().getLastName());
        contact.setText(userIdController.getUser().getContact());
        Picasso.get().load(userIdController.getUser().getPicture()).into(profPic);

        profPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UserIdController.selectImage(ProfileActivity.this);
            }


        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setOnProfileUpdateListener(ProfileActivity.this);

                editProfileFragment.show(getSupportFragmentManager(), "profileEditDialog");

            }


        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            Uri imageUri = data.getData();

            userIdController.uploadProfilePicture(imageUri);
            Picasso.get().load(imageUri).into(profPic);



        }
    }




    @Override
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact, Uri newPicture) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        contact.setText(newContact);
        Picasso.get().load(newPicture).into(profPic);

    }
}

