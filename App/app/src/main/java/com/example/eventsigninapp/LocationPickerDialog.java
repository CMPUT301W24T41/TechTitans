package com.example.eventsigninapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class LocationPickerDialog extends DialogFragment implements OnMapReadyCallback {
    GoogleMap map;
    Location selectedLocation;
    DialogCloseListener listener;
    String locationQuery;
    List<Address> addresses;
    UiSettings mapUI;
    final int ZOOM_LEVEL = 15;

    public LocationPickerDialog(DialogCloseListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.dialog_pick_location, null);

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_selection_fragment);
        assert mapFrag != null;
        mapFrag.getMapAsync(this);

        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (bundle == null) {
            Log.e("PICKER", "null bundle");
        } else { // search location
            Geocoder geocoder = new Geocoder(requireContext());
            locationQuery = bundle.getString("query");
            assert locationQuery != null;
            try {
                addresses = geocoder.getFromLocationName(locationQuery, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return builder
                .setView(v)
                .setTitle("Select a location")
                .setMessage("Click on a marker to confirm the location for your event.")
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
        mapUI = map.getUiSettings();
        mapUI.setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                selectedLocation = new Location("prov");
                LatLng markerPosition = marker.getPosition();
                selectedLocation.setLatitude(markerPosition.latitude);
                selectedLocation.setLongitude(markerPosition.longitude);
                marker.showInfoWindow();
                return true;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                selectedLocation = new Location("prov");
                selectedLocation.setLatitude(latLng.latitude);
                selectedLocation.setLongitude(latLng.longitude);
                map.addMarker(new MarkerOptions().position(latLng));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            }
        });
        showResultOnMap();
    }


    private void showResultOnMap() {
        if (addresses.size() == 0) {
            return;
        }

        Address address = addresses.get(0);
        LatLng point = new LatLng(address.getLatitude(), address.getLongitude());
        map.addMarker(new MarkerOptions().position(point).title(locationQuery));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, ZOOM_LEVEL));
    }


    interface DialogCloseListener {
        void onDialogClose(Location location);
    }
}
