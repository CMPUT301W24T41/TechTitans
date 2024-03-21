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
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class EventArrayAdapter extends ArrayAdapter<Event> implements DatabaseController.EventImageUriCallbacks{
    DatabaseController databaseController = new DatabaseController();

    private ArrayList<Event> events;
    private Context context;

    private int layoutID;

    private ImageView eventPoster;


    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;

    }

    public EventArrayAdapter(Context context, int layoutID, ArrayList<Event> events) {
        super(context, layoutID, events);
        this.events = events;
        this.context = context;
        this.layoutID = layoutID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (layoutID == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);

            Event event = events.get(position);
            Log.e("DEBUG", String.format("Looking at %s", event.getName()));

            TextView eventTitle = view.findViewById(R.id.event_title);
            TextView eventDescription = view.findViewById(R.id.event_description);

            eventTitle.setText(event.getName());
            eventDescription.setText(event.getDescription());
        } else if (layoutID == R.layout.admin_event_list_item) {
            view = LayoutInflater.from(context).inflate(layoutID, parent, false);

            Event event = events.get(position);

            TextView eventTitle = view.findViewById(R.id.adminViewEventName);
            TextView organizerID = view.findViewById(R.id.adminViewOrganizerID);
            TextView eventID = view.findViewById(R.id.adminViewEventID);
            TextView eventCapacity = view.findViewById(R.id.adminViewEventCapacity);
            eventPoster = view.findViewById(R.id.adminViewEventPosterImage);
            Button deleteButton = view.findViewById(R.id.adminViewDeleteEvent);


            databaseController.getEventPoster(event.getUuid(), this);

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


        }


        return view;
    }

    public void onEventPosterCallback(Uri uri) {
        // Handle the retrieved URI here
        String imageUrl = uri.toString();
        Picasso.get().load(imageUrl).into(eventPoster);
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
        //load dummy picture
//        Picasso.get().load(R.drawable.user).into(profilePic);
    }
}
