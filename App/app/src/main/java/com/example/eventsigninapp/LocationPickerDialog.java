package com.example.eventsigninapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationPickerDialog extends DialogFragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    GoogleMap map;
    Location selectedLocation;
    DialogCloseListener listener;
    String locationQuery;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogCloseListener) {
            listener = (DialogCloseListener) context;
        } else {
            throw new RuntimeException(context + " must implement DialogCloseListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pick_location, null);

        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (bundle == null) {
            Log.e("PICKER", "null bundle");
        } else { // search location
            locationQuery = bundle.getString("query");
            List<Address> addresses;
            assert locationQuery != null;
            if (!locationQuery.equals("")) {
                Geocoder geocoder = new Geocoder(requireContext());
                try {
                    addresses = geocoder.getFromLocationName(locationQuery, 5);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return builder
                .setView(v)
                .setTitle("Select a location")
                .setMessage("Set a location for your event.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogClose(selectedLocation);
                    }
                })
                .create();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        selectedLocation = new Location("prov");
        selectedLocation.setLatitude(latLng.latitude);
        selectedLocation.setLongitude(latLng.longitude);
    }

    interface DialogCloseListener {
        void onDialogClose(Location location);
    }
}
