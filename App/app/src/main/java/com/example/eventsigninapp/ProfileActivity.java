package com.example.eventsigninapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class ProfileActivity extends AppCompatActivity implements EditProfileFragment.OnProfileUpdateListener{

    TextView firstName;
    TextView lastName;
    TextView phoneNumber;

    UserIdController userIdController = new UserIdController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        getSupportFragmentManager().beginTransaction().add(R.id.toolbarFragmentContainer, new ToolbarFragment()).commit();

        Button editButton = findViewById(R.id.editButton);
        firstName = findViewById(R.id.user_first_name);
        lastName = findViewById(R.id.user_last_name);
        phoneNumber = findViewById(R.id.user_number);

        firstName.setText(userIdController.getUser().getFirstName());
        lastName.setText(userIdController.getUser().getLastName());
        phoneNumber.setText(userIdController.getUser().getContact());


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
    public void onProfileUpdate(String newFirstName, String newLastName, String newContact) {
        firstName.setText(newFirstName);
        lastName.setText(newLastName);
        phoneNumber.setText(newContact);

    }
}

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK) {
//            Uri uri = data.getData();
//
//            userIdController.getUser().setPicture(uri);
//
//        }
//
//
//    }