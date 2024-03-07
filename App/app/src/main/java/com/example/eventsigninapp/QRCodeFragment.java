package com.example.eventsigninapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import java.io.ByteArrayOutputStream;

public class QRCodeFragment extends DialogFragment {

    private static final String ARG_QR_CODE_BITMAP = "arg_qr_code_bitmap";

    private ImageView qrCodeImageView;

    private Button btnOk;
    private Button btnCancel;

    public QRCodeFragment() {
        // Required empty public constructor
    }

    public static QRCodeFragment newInstance(Bitmap qrCodeBitmap) {
        QRCodeFragment fragment = new QRCodeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QR_CODE_BITMAP, qrCodeBitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_qrcode, container, false);
        qrCodeImageView = rootView.findViewById(R.id.qrCodeImageView);
        btnOk = rootView.findViewById(R.id.btnShare);
        btnCancel = rootView.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the QR code bitmap from arguments
                Bitmap qrCodeBitmap = getArguments().getParcelable(ARG_QR_CODE_BITMAP);

                // Create intent with ACTION_SEND
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/jpeg");


                // Convert Bitmap to byte array
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), qrCodeBitmap, "QR Code", null);
                Uri qrCodeUri = Uri.parse(path);

                // Set the URI of the QR code image to be shared
                shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);

                // Start activity with chooser
                startActivity(Intent.createChooser(shareIntent, "Share via"));

                dismiss(); // Close the dialog
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Cancel button click, if needed
                dismiss(); // Close the dialog
            }
        });


        // Load QR code bitmap from arguments
        Bitmap qrCodeBitmap = getArguments().getParcelable(ARG_QR_CODE_BITMAP);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        }

        return rootView;
    }
}
