package com.example.eventsigninapp;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.Executor;

public class CheckInConfirmationDialog extends AlertDialog {
    private final View rootView;
    private final ImageView imageView;
    private final SwitchMaterial toggleTracking;
    private final FusedLocationProviderClient fusedLocationClient;
    private final DatabaseController dbController;

    public CheckInConfirmationDialog(Context context, ViewGroup parent) {
        super(context);

        rootView = getLayoutInflater().inflate(R.layout.check_in_confirmation, parent, false);

        imageView = rootView.findViewById(R.id.checkInConfirmImage);
        toggleTracking = rootView.findViewById(R.id.tracking_permission);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        dbController = new DatabaseController();
    }

    public void showEvent(Event event, ConfirmationListener listener) {
        setView(rootView);
        setTitle(event.getName());
        setMessage("You have successfully checked in to this event!");
        setCancelable(true);
        setButton(BUTTON_POSITIVE, "OK", (dialog, which) -> {
            dialog.dismiss();
            Log.e("LOCATION", String.valueOf(toggleTracking.isChecked()));
            listener.isLocationPermissionGranted(toggleTracking.isChecked());
        });
        if (event.getPosterUri() != null) {
            Picasso.get().load(event.getPosterUri()).into(imageView);
        }
        else {
            // Load the default event image
            Picasso.get().load(R.drawable.event_image).into(imageView);
        }

        show();
    }

    public interface ConfirmationListener {
        void isLocationPermissionGranted(boolean granted);
    }

}