package com.example.eventsigninapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CheckInView {
    private ScanButtonListener listener;
    private final View rootView;

    public CheckInView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_check_in, parent, false);

        Button scanButton = rootView.findViewById(R.id.scanButton);

        scanButton.setOnClickListener(v -> onButtonClick());
    }

    public void setListener(ScanButtonListener listener) {
        this.listener = listener;
    }

    public View getRootView() {
        return rootView;
    }

    public void onButtonClick() {
        assert listener != null;
        listener.onScanButtonClick();
    }

    interface ScanButtonListener {
        void onScanButtonClick();
    }
}
