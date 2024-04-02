package com.example.eventsigninapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Set;

public class AdminEventArrayAdapter extends ArrayAdapter<Event> implements DatabaseController.EventImageUriCallbacks{
    DatabaseController databaseController = new DatabaseController();

    private ArrayList<Event> events;
    private Context context;

    private int layoutID;

    private ImageView eventPoster;



    public AdminEventArrayAdapter(Context context, int layoutID, ArrayList<Event> events) {
        super(context, layoutID, events);
        this.events = events;
        this.context = context;
        this.layoutID = layoutID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (layoutID != 0) {
            view = LayoutInflater.from(context).inflate(layoutID, parent, false);

            Event event = events.get(position);

            TextView eventTitle = view.findViewById(R.id.adminViewEventName);
            TextView organizerID = view.findViewById(R.id.adminViewOrganizerID);
            TextView eventID = view.findViewById(R.id.adminViewEventID);
            TextView eventCapacity = view.findViewById(R.id.adminViewEventCapacity);
            eventPoster = view.findViewById(R.id.adminViewEventPosterImage);
            Button deleteButton = view.findViewById(R.id.adminViewDeleteEvent);

            ImageView deleteImage = view.findViewById(R.id.adminViewEventXButton);

//            eventPoster.setTag(position);

            databaseController.getEventPoster(event.getUuid(), eventPoster, this);

            eventTitle.setText(event.getName());
            organizerID.setText(String.format(context.getString(R.string.organizerid), event.getCreatorUUID()));
            eventID.setText(String.format(context.getString(R.string.eventid), event.getUuid()));
            eventCapacity.setText(String.format(context.getString(R.string.division), event.getSignedUpUsersUUIDs().size(), event.getCapacity()));


            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    events.remove(event);
                    notifyDataSetChanged();
                    databaseController.deleteEvent(event);
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    events.get(position).setPosterUri(null);
                    databaseController.deleteEventPicture(event);
                    notifyDataSetChanged();
                }
            });




        }


        return view;
    }



    @Override
    public void onEventPosterCallback(Uri imageUri, ImageView imageView) {
        Log.d("testest", "onEventPosterCallback: " + imageUri + imageView);


        if (imageUri != null) {
            Picasso.get().load(imageUri).into(imageView);
        } else {
            // Load a placeholder image if URI is null
            imageView.setImageResource(R.drawable.event_image);
        }

    }


    @Override
    public void onEventPosterCallback(Uri imageUri) {
        Log.d("testest", "onEventPosterCallback2: " + imageUri);
        return;
    }

    @Override
    public void onEventCheckInQRCodeCallback(Uri imageUri) {
        return;
    }

    @Override
    public void onEventDescriptionQRCodeCallback(Uri imageUri) {
        return;
    }

    @Override
    public void onError(Exception e) {
        return;
    }
}