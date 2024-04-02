package com.example.eventsigninapp;

import android.content.Intent;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
DatabaseController.GetCheckInLocationCallback,
DatabaseController.GetEventCallback {
    Button backButton;
    TextView eventTitle;
    DatabaseController dbController;
    ArrayList<Location> locations;
    Event event;
    GoogleMap map;
    String testUuid = "0f3389e5-1b6c-4c3a-b2be-88d5d5ef0260";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        eventTitle = findViewById(R.id.event_title);
        backButton = findViewById(R.id.back_button);
        dbController = new DatabaseController();
        locations = new ArrayList<Location>();
        event = new Event();
        event.setUuid(testUuid);

        // dbController.getEventFromFirestore(testUuid, this);
        dbController.getCheckInLocationsFromFirestore(event, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        backButton.setOnClickListener(listener -> {
            Intent goBack = new Intent(MapActivity.this, AttendeeListActivity.class);
            startActivity(goBack);
        });
    }

    private void addMarkersToMap() {
        LatLng center = getAvgLatLng();
        for (Location loc : locations) {
            LatLng point = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.addMarker(new MarkerOptions().position(point));
        }
        map.moveCamera(CameraUpdateFactory.newLatLng(center));
    }

    private LatLng getAvgLatLng() {
        double totalLat = 0, totalLng = 0;
        for (Location loc : locations) {
            totalLat += loc.getLatitude();
            totalLng += loc.getLongitude();
        }
        return new LatLng(totalLat / locations.size(), totalLng / locations.size());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onGetCheckInLocationCallback(Event event, ArrayList<?> checkInLocations) {
        for (int i = 0; i < checkInLocations.size(); i++) {
            GeoPoint gPoint = (GeoPoint) checkInLocations.get(i);
            Location loc = new Location("prov");
            loc.setLatitude(gPoint.getLatitude());
            loc.setLongitude(gPoint.getLongitude());
            locations.add(loc);
        }

        addMarkersToMap();
    }

    @Override
    public void onGetEventCallback(Event event) {
        this.event = event;
        eventTitle.setText(event.getName());
    }
}
