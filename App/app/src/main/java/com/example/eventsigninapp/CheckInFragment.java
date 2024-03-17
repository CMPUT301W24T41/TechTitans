package com.example.eventsigninapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * This class acts as a controller for the check in process.
 * It is responsible for scanning QR codes and processing the result.
 */
public class CheckInFragment extends Fragment implements CheckInView.ScanButtonListener, DatabaseController.GetEventCallback, DatabaseController.EventImageUriCallbacks {
    private DatabaseController databaseController;
    private CheckInConfirmationDialog checkInConfirmationDialog;
    private Event event;

    private ActivityResultLauncher<ScanOptions> scanLauncher;

    // Variable to track the number of scans
    private int scanCount = 0;
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Called when the fragment is created.
     * Initialize the ActivityResultLauncher for scanning QR codes.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanLauncher = registerForActivityResult(new ScanContract(), this::processResult);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
    }

    /**
     * Processes the result of the barcode scanner activity.
     * Shows the scanned result in an alert dialog.
     */
    private void processResult(ScanIntentResult result) {
        if (result.getContents() != null) {
            scanCount++;
            System.out.println(scanCount);

            databaseController = new DatabaseController();
            databaseController.getEventFromFirestore(result.getContents(), this);
            getUserLocation(result);
        }
    }

    /**
     * This method gets the user location
     */
    private void getUserLocation(ScanIntentResult result) {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && result != null) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                databaseController.addCheckInLocationToFirestore(event, location);
                            } else {
                                Log.e("DEBUG", "Failed to fetch location");
                            }
                        }
                    });
        }
    }

    private void showCheckInConfirmation() {
        if (event != null) {
            checkInConfirmationDialog.showEvent(event);
        }
    }

    @Override
    public void onGetEventCallback(Event event) {
        this.event = event;
        EventController eventController = new EventController(event);
        if (event != null) {
            UserController userController = new UserController();
            User user = userController.getUser();

            try {
                eventController.checkInUser(user.getId());
                databaseController.pushEventToFirestore(event);
            } catch (Exception e) {
                e.printStackTrace();
            }

            databaseController.getEventPoster(event.getUuid(), this);
        }
    }

    /**
     * Called to create the view for the fragment.
     * Initializes the CheckInView and sets the listener.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CheckInView checkInView = new CheckInView(inflater, container);
        checkInView.setListener(this);

        checkInConfirmationDialog = new CheckInConfirmationDialog(requireContext(), container);

        return checkInView.getRootView();
    }

    /**
     * Launches the barcode scanner activity to scan QR codes.
     */
    @Override
    public void onScanButtonClick() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setCaptureActivity(com.example.eventsigninapp.CaptureAct.class);
        scanLauncher.launch(options);
    }

    @Override
    public void onEventPosterCallback(Uri imageUri) {
        if (!(imageUri == null)) {
            event.setPosterUri(imageUri);
        }

        showCheckInConfirmation();
    }

    @Override
    public void onEventCheckInQRCodeCallback(Uri imageUri) {
        event.setCheckInQRCodeUri(imageUri);
    }

    @Override
    public void onEventDescriptionQRCodeCallback(Uri imageUri) {
        event.setDescriptionQRCodeUri(imageUri);
    }

    @Override
    public void onError(Exception e) {
    }
}