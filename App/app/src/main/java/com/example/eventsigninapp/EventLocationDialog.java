package com.example.eventsigninapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

public class EventLocationDialog extends DialogFragment implements OnMapReadyCallback {
    GoogleMap map;
    Event event;
    GeoPoint eventLocation;
    UiSettings mapUI;
    final int ZOOM_LEVEL = 15;

    public EventLocationDialog() {
        // empty constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.dialog_event_location, null);

        SupportMapFragment mapFrag = (SupportMapFragment)  getChildFragmentManager().findFragmentById(R.id.event_location_map_fragment);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);

        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (bundle == null) {
            Log.e("EVENTLOC", "null bundle");
        } else {
            event = (Event) bundle.get("event");
        }

        return builder
                .setView(v)
                .setTitle(event.getName())
                .setMessage(event.getDescription())
                .setPositiveButton("DONE", (dialog, which) -> this.dismiss())
                .create();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        mapUI = map.getUiSettings();
        mapUI.setZoomControlsEnabled(true);

        assert event != null;
        eventLocation = event.getLocation();
        if (eventLocation != null) {
            LatLng point = new LatLng(eventLocation.getLatitude(), eventLocation.getLongitude());

            map.addMarker(new MarkerOptions().position(point).title(event.getName()));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, ZOOM_LEVEL));
        }
    }
}
