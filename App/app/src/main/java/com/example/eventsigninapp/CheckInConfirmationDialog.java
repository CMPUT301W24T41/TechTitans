package com.example.eventsigninapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

public class CheckInConfirmationDialog extends AlertDialog {
    private final View rootView;
    private final ImageView imageView;

    public CheckInConfirmationDialog(Context context, ViewGroup parent) {
        super(context);

        rootView = getLayoutInflater().inflate(R.layout.check_in_confirmation, parent, false);

        imageView = rootView.findViewById(R.id.imageView);
    }

    public void showEvent(Event event) {
        setView(rootView);
        setTitle(event.getName());
        setMessage("You have successfully checked in to this event!");
        setCancelable(true);
        setButton(BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        if (event.getPosterUri() != null) {
            Picasso.get().load(event.getPosterUri()).into(imageView);
            System.out.println("YOOOOOOOOOOOOOO");
        }
        System.out.println("NOPE");
        show();
    }
}