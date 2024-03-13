package com.example.eventsigninapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class NotifyUsersBottomSheetFragment extends BottomSheetDialogFragment {

    EditText message, title;
    Spinner spinnerOptions;
    Button sendButton;

    public NotifyUsersBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notify_users_bottom_sheet, container, false);

        // Initialize views
        sendButton = view.findViewById(R.id.sendButton);
        message = view.findViewById(R.id.editTextMessage);
        title = view.findViewById(R.id.editTextTitle);
        spinnerOptions = view.findViewById(R.id.spinnerFilter);

        // Return the inflated view
        return view;
    }

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String messageText = message.getText().toString();
//                String titleText = title.getText().toString();
//                String filter = spinnerOptions.getSelectedItem().toString();
//            }




    }


