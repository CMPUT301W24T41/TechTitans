package com.example.eventsigninapp;

import static com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

/**
 * This class acts as a controller for the check in process.
 * It is responsible for scanning QR codes and processing the result.
 */
public class CheckInFragment extends Fragment implements CheckInView.ScanButtonListener, DatabaseController.GetEventCallback, DatabaseController.EventImageUriCallbacks, CheckInConfirmationDialog.ConfirmationListener {
    private DatabaseController databaseController;
    private CheckInConfirmationDialog checkInConfirmationDialog;
    private Event event;
    private String resultString;

    private ActivityResultLauncher<ScanOptions> scanLauncher;

    // Variable to track the number of scans
    private int scanCount = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locationPermissionGranted;
    private final int FINE_PERMISSION_CODE = 1;

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
    private void processResult(ScanIntentResult newResult) {
        if (newResult.getContents() != null) {
            this.resultString = newResult.getContents();
            scanCount++;
            System.out.println(scanCount);

            databaseController = new DatabaseController();
            databaseController.findEventByQrResult(resultString, this);

        }
    }

    private void showCheckInConfirmation() {
        System.out.println(event);
        if (event != null) {
            checkInConfirmationDialog.showEvent(event, this);
        } else {
            // Handle the case where the event is null
            // or it does not exist anymore
            Log.e("LOCATION", "Event is null!");
        }
    }

    /**
     * This method verifies if an attendee is physically present
     * to actually check into the event.
     * @return locationVerified true if an attendee can check in, false otherwise
     */
    private boolean verifyUserLocation(Location userLocation) {
        Location eventLocation = null;
        if (event == null) {
            Log.e("LOCATION", "null event in verify user location");
            return false;
        } else {
            eventLocation = event.getLocation();
        }
        if (eventLocation == null) {
            Log.e("LOCATION", "location verification is turned off; location null");
            Toast.makeText(getContext(), "Location tracking is disabled for this event.", Toast.LENGTH_LONG).show();
            return false;
        }

        double latDiff = Math.abs(eventLocation.getLatitude() - userLocation.getLatitude());
        double lngDiff = Math.abs(eventLocation.getLongitude() - userLocation.getLongitude());

        // using 111.1 km = 1 degree
        // must be within 3km to check in
        return (0.027 <= latDiff && 0.027 <= lngDiff);
    }


    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        if ((ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && locationPermissionGranted) {
            try {
                Task<Location> locationTask = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null);
                locationTask.addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location location = task.getResult();
                            if (location != null) {
                                Log.e("LOCATION", "Location retrieved: " + location);
                                boolean isNearby = verifyUserLocation(location);
                                if (!isNearby) {
                                    Toast.makeText(getContext(), "You must be within 3 km of the event to check in.", Toast.LENGTH_LONG).show();
                                }
                                databaseController.addCheckInLocationToFirestore(event, location);
                            } else {
                                Log.e("LOCATION", "Failed to fetch location");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("LOCATION", "Error retrieving location" + e.getMessage());
            }
        } else {
            Log.e("LOCATION", "Tracking not permitted");
        }
    }


    @Override
    public void onGetEventCallback(Event event) {
        if (event != null) {
            this.event = event;
            if (!Objects.equals(event.getUuid(), this.resultString)) {
                openEventDetails();
            } else {
                UserController userController = new UserController();
                User user = userController.getUser();
                EventController eventController = new EventController(event);

                try {
                    eventController.checkInUser(user.getId());
                    databaseController.pushEventToFirestore(event);
                    showCheckInConfirmation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Opens the event details fragment.
     */
    private void openEventDetails() {
        EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        eventDetailsFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, eventDetailsFragment)
            .addToBackStack(null)
            .commit();
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
        event.setPosterUri(imageUri);
        if (!(imageUri == null)) {
            event.setPosterUri(imageUri);
        }
        // showCheckInConfirmation();
    }

    @Override
    public void onEventPosterCallback(Uri imageUri, ImageView imageView) {
        return;
    }

    @Override
    public void onEventCheckInQRCodeCallback(Uri imageUri) {
        event.setCheckInQRCodeUri(imageUri);
    }

    @Override
    public void onEventDescriptionQRCodeCallback(Uri imageUri) {
        event.setDetailsQRCodeUri(imageUri);
    }

    @Override
    public void onError(Exception e) {
        // showCheckInConfirmation();
        // System.out.println("onError");
    }

    @Override
    public void isLocationPermissionGranted(boolean granted) {
        locationPermissionGranted = granted;
        if (event != null) {
            getUserLocation();
        } else {
            Toast.makeText(getContext(), "Error: please try again", Toast.LENGTH_LONG).show();
        }
    }

}