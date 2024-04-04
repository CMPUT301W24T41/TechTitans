package com.example.eventsigninapp;

// Import statements

import android.content.Context;
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

public class AdminEventArrayAdapter extends ArrayAdapter<Event> implements DatabaseController.EventImageUriCallbacks {

    // Member variables
    private ArrayList<Event> events;
    private Context context;
    private int layoutID;
    private ImageView eventPoster;
    private DatabaseController databaseController = new DatabaseController();

    // Constructor
    public AdminEventArrayAdapter(Context context, int layoutID, ArrayList<Event> events) {
        super(context, layoutID, events);
        this.events = events;
        this.context = context;
        this.layoutID = layoutID;
    }

    // Method to populate list view item
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (layoutID != 0) {
            view = LayoutInflater.from(context).inflate(layoutID, parent, false);

            // Retrieve event object for current position
            Event event = events.get(position);

            // Find views within list item layout
            TextView eventTitle = view.findViewById(R.id.adminViewEventName);
            TextView organizerID = view.findViewById(R.id.adminViewOrganizerID);
            TextView eventID = view.findViewById(R.id.adminViewEventID);
            TextView eventCapacity = view.findViewById(R.id.adminViewEventCapacity);
            eventPoster = view.findViewById(R.id.adminViewEventPosterImage);
            Button deleteButton = view.findViewById(R.id.adminViewDeleteEvent);
            ImageView deleteImage = view.findViewById(R.id.adminViewEventXButton);

            // Load event poster image using Picasso
            databaseController.getEventPoster(event.getUuid(), eventPoster, this);

            // Populate views with event data
            eventTitle.setText(event.getName());
            organizerID.setText(String.format(context.getString(R.string.organizerid), event.getCreatorUUID()));
            eventID.setText(String.format(context.getString(R.string.eventid), event.getUuid()));
            eventCapacity.setText(String.format(context.getString(R.string.division), event.getSignedUpUsersUUIDs().size(), event.getCapacity()));

            // Set onClickListener for delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove event from list and notify adapter
                    events.remove(event);
                    notifyDataSetChanged();
                    // Delete event from database
                    databaseController.deleteEvent(event);
                }
            });

            // Set onClickListener for delete image
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set poster URI to null, remove image, and notify adapter
                    events.get(position).setPosterUri(null);
                    databaseController.deleteEventPicture(event);
                    notifyDataSetChanged();
                }
            });
        }

        return view;
    }

    // Callback method for event poster URI
    @Override
    public void onEventPosterCallback(Uri imageUri, ImageView imageView) {
        if (imageUri != null) {
            // Load event poster image using Picasso
            Picasso.get().load(imageUri).into(imageView);
        } else {
            // Load a placeholder image if URI is null
            imageView.setImageResource(R.drawable.event_image);
        }
    }

    // Unused callback methods
    @Override
    public void onEventPosterCallback(Uri imageUri) {
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
