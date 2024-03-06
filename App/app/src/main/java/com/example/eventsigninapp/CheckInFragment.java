package com.example.eventsigninapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckInFragment#} factory method to
 * create an instance of this fragment.
 */
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckInFragment#} factory method to
 * create an instance of this fragment.
 */
public class CheckInFragment extends Fragment {

    ActivityResultLauncher<ScanOptions> barLauncher;

    // Variable to track the number of scans
    private int scanCount = 0;

    /**
     * Required empty public constructor for the fragment.
     */
    public CheckInFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created.
     * Initialize the ActivityResultLauncher for scanning QR codes.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {

                        scanCount++;
                        System.out.println(scanCount);

                        // Show the scanned result in an alert dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Result");
                        builder.setMessage("Scanned Content: " + result.getContents() + "\nScan Count: " + scanCount);

                        // The custom view to the AlertDialog
                        LayoutInflater inflater = LayoutInflater.from(requireContext());
                        View dialogView = inflater.inflate(R.layout.custom_camera_dialog_layout, null);

                        ImageView imageView = dialogView.findViewById(R.id.imageView);
                        imageView.setImageResource(R.drawable.event_image);
                        // Set the custom view to the AlertDialog
                        builder.setView(dialogView);

                        builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                });
    }

    /**
     * Called to create the view for the fragment.
     * Initializes UI components and sets onClickListener for scan button.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_check_in, container, false);

        Button scanButton = rootView.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        return rootView;
    }

    /**
     * Launches the barcode scanner activity to scan QR codes.
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        // Adjust this line if your CaptureAct class has a different name or package
        options.setCaptureActivity(com.example.eventsigninapp.CaptureAct.class);
        barLauncher.launch(options);
    }
}
