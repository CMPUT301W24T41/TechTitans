package com.example.eventsigninapp;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class Organizer extends User {
    private String id;
    private String phoneNumber;
    // Attributes
    private String name;
    private List<Event> eventsOrganized;
    private boolean geolocationEnabled;



    // Constructor
    public Organizer() {
        super();
        this.eventsOrganized = new ArrayList<>();
        this.geolocationEnabled = false; // By default, geolocation is disabled
    }

    // Methods
    public void createEvent(Event eventDetails) {
        // Implement event creation logic here
        eventsOrganized.add(eventDetails);
    }

    public void uploadEventPoster(Event event, String poster) {
        // Implement event poster uploading logic here

        //event.setPoster(poster);
    }

    public static Bitmap generateQRCode(String eventName) {
        // Implement QR code generation logic here
        Bitmap qrCode = generateUniqueQRCode(eventName);

        //To do
        // Do something with the generated QR code, such as saving it to the event object
        // For example:
        // Event event = new Event(eventName, eventDate, eventLocation);
        // event.setQrCode(qrCode);
        return qrCode;
    }

    private static Bitmap  generateUniqueQRCode(String eventName) {
        // Generate a unique QR code based on event details
        MultiFormatWriter writer = new MultiFormatWriter();
        Bitmap bitmap = null; // Initialize bitmap
        try {

            BitMatrix matrix = writer.encode(eventName, BarcodeFormat.QR_CODE, 600,600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }

    public void enableGeolocationVerification() {
        geolocationEnabled = true;
    }

    public void disableGeolocationVerification() {
        geolocationEnabled = false;
    }
}

