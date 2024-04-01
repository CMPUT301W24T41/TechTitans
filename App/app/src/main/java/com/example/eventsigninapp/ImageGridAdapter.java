package com.example.eventsigninapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageGridAdapter extends ArrayAdapter<Uri> {
    private Context Context;
    private List<Uri> ImageUrls; // List of image URLs

    private DatabaseController databaseController = new DatabaseController();
    private int layoutID;

    public ImageGridAdapter(Context context, int layoutID, List<Uri> imageUrls) {
        super(context, 0, imageUrls);
        Context = context;
        layoutID = layoutID;
        ImageUrls = imageUrls;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View gridItemView = convertView;
        if (gridItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.single_image, parent, false);
        }

        // Load image into ImageView using Picasso
        ImageView imageView = gridItemView.findViewById(R.id.gridImage);
        ImageView xButton = gridItemView.findViewById(R.id.gridImageXButtons);
        Log.d("success found", "getView: loading:" + ImageUrls.get(position));
        Picasso.get().load(ImageUrls.get(position)).into(imageView);


        xButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ImageUrls.remove(position);
                notifyDataSetChanged();

                databaseController.deleteImageFromUri(ImageUrls.get(position));

            }
        });
        return gridItemView;
    }
}
