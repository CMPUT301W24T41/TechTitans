package com.example.eventsigninapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * This class acts as a controller for the check in process.
 * It is responsible for scanning QR codes and processing the result.
 */
public class CheckInFragment extends Fragment implements CheckInView.ScanButtonListener {
    private ScanIntentResult result;
    private CheckInView checkInView;

    ActivityResultLauncher<ScanOptions> scanLauncher;

    // Variable to track the number of scans
    private int scanCount = 0;

    /**
     * Called when the fragment is created.
     * Initialize the ActivityResultLauncher for scanning QR codes.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanLauncher = registerForActivityResult(new ScanContract(), this::processResult);
    }

    /**
     * Processes the result of the barcode scanner activity.
     * Shows the scanned result in an alert dialog.
     */
    private void processResult(ScanIntentResult result) {
        if (result.getContents() != null) {
            scanCount++;
            System.out.println(scanCount);

            this.result = result;

            createAlertDialog();
        }
    }

    private void createAlertDialog() {
        ScanResultAlertDialog dialog = new ScanResultAlertDialog(requireContext(), (ViewGroup) checkInView.getRootView());
        dialog.setImageView(R.drawable.event_image);
        dialog.showResult(result.getContents(), scanCount);
    }

    /**
     * Called to create the view for the fragment.
     * Initializes the CheckInView and sets the listener.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        checkInView = new CheckInView(inflater, container);
        checkInView.setListener(this);

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
}