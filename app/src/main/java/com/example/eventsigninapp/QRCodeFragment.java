package com.example.eventsigninapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class QRCodeFragment extends AlertDialog {

    private static final String ARG_QR_CODE_BITMAP = "arg_qr_code_bitmap";
    private ImageView qrCodeImageView;
    private View rootView;

    public QRCodeFragment(Context context, ViewGroup parent, Bitmap qrCodeBitmap) {
        super(context);

        rootView = LayoutInflater.from(context).inflate(R.layout.fragment_qrcode, parent, false);

        qrCodeImageView = rootView.findViewById(R.id.qrCodeImageView);

        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        setButton(BUTTON_POSITIVE, "Share", (dialog, which) -> {
            // Create intent with ACTION_SEND
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");


            // Convert Bitmap to byte array
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), qrCodeBitmap, "QR Code", null);
            Uri qrCodeUri = Uri.parse(path);

            // Set the URI of the QR code image to be shared
            shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);

            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        setButton(BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            // Handle Cancel button click, if needed
            dismiss(); // Close the dialog
        });
    }

    public void show() {
        setView(rootView);
        setCancelable(true);
        super.show();
    }
}
