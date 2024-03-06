package com.example.eventsigninapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class CheckInActivity extends AppCompatActivity {
    private AppCompatButton hostButton;
    private ConstraintLayout qrCodeLayout;

    public CheckInActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        hostButton = findViewById(R.id.checkInHostEventButton);

        hostButton.setOnClickListener(v -> {
            // TODO: Add logic to host an event
        });

        qrCodeLayout = findViewById(R.id.checkInQrConstraintLayout);

        qrCodeLayout.setOnClickListener(v -> {
            Intent k = new Intent(CheckInActivity.this, QRCodeScannerActivity.class);
            startActivity(k);
            // TODO: Add logic to scan a QR code
        });
    }
}
