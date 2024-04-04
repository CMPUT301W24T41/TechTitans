package com.example.eventsigninapp;

// Import statements

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AdminImageListFragment extends Fragment implements DatabaseController.GetAllImagesCallback {

    // Member variables
    DatabaseController databaseController = new DatabaseController();
    ArrayList<Uri> images;
    GridView imageGrid;
    ImageGridAdapter imageGridAdapter;

    // Default constructor
    public AdminImageListFragment(){};

    // onCreate method
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_image_grid, container, false);

        // Find views
        imageGrid = view.findViewById(R.id.allImagesGrid);

        // Initialize images list and adapter for grid view
        images = new ArrayList<Uri>();
        imageGridAdapter = new ImageGridAdapter(getContext(), R.layout.single_image, images);
        imageGrid.setAdapter(imageGridAdapter);

        // Retrieve all images from Firestore database
        databaseController.getAllImagesFromFirestore(this);
        Log.d("success found", "onCreateView: " + images);

        return view;
    }

    // Callback method for getting all images from Firestore database
    @Override
    public void onGetAllImagesCallback(ArrayList<Uri> allImages) {
        // Clear the current list of images and add all retrieved images
        images.clear();
        images.addAll(allImages);
        // Notify the adapter that the dataset has changed
        imageGridAdapter.notifyDataSetChanged();
    }
}
