package com.example.eventsigninapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;

public class ProfileActivity extends AppCompatActivity {



    UserIdController userIdController = new UserIdController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        getSupportFragmentManager().beginTransaction().add(R.id.toolbarFragmentContainer, new ToolbarFragment()).commit();

        Button editButton = findViewById(R.id.editButton);
        TextView firstName = findViewById(R.id.user_first_name);
        TextView lastName = findViewById(R.id.user_last_name);
        TextView phoneNumber = findViewById(R.id.user_number);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 UserIdController.selectImage(ProfileActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                userIdController.getUser().setPicture(uri);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }


    }

}