package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(listener -> {
            Intent goBack = new Intent(MapActivity.this, AttendeeListActivity.class);
            startActivity(goBack);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng edmonton = new LatLng(53.5461, -113.4937);
        googleMap.addMarker(new MarkerOptions()
                .position(edmonton)
                .title("Marker in Edmonton"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(edmonton));
    }
}
