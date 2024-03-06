package com.example.eventsigninapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

public class ScanResultAlertDialog extends AlertDialog {
    private View rootView;
    private ImageView imageView;

    public ScanResultAlertDialog(Context context, ViewGroup parent) {
        super(context);

        rootView = getLayoutInflater().inflate(R.layout.scan_result_dialog, parent, false);

        imageView = rootView.findViewById(R.id.imageView);
    }

    public void showResult(String result, int scanCount) {
        setView(rootView);
        setTitle("Result");
        setMessage("Scanned Content: " + result + "\nScan Count: " + scanCount);
        setCancelable(true);
        setButton(BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        show();
    }

    public void setImageView(int imageResource) {
        imageView.setImageResource(imageResource);
    }
}