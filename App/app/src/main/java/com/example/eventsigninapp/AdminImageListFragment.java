package com.example.eventsigninapp;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AdminImageListFragment extends Fragment implements DatabaseController.GetAllImagesCallback{


    DatabaseController databaseController = new DatabaseController();

    ArrayList<Uri> images;

    GridView imageGrid;

    ImageGridAdapter imageGridAdapter;

    public AdminImageListFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_image_grid, container, false);



        imageGrid = view.findViewById(R.id.allImagesGrid);

        images = new ArrayList<Uri>();
        imageGridAdapter = new ImageGridAdapter(getContext(), R.layout.single_image, images);
        imageGrid.setAdapter(imageGridAdapter);

        databaseController.getAllImagesFromFirestore(this);
        Log.d("success found", "onCreateView: " + images);

        return view;
    }


    @Override
    public void onGetAllImagesCallback(ArrayList<Uri> allImages) {
        images.clear();
        images.addAll(allImages);
        imageGridAdapter.notifyDataSetChanged();
//        Log.d("success found", "onCreateView: " + images);

    }
}


