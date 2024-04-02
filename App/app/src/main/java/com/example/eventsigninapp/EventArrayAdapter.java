package com.example.eventsigninapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EventArrayAdapter extends RecyclerView.Adapter {
    private ArrayList<Event> events;
    private Context context;
    private UserController userController;

    private OnItemClickListener onItemClickListener;
    private Event checkEvent;
    private ArrayList<String> checkedInUsers;

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventPoster;
        private TextView eventTitle;
        private TextView eventDescription;
        private LinearLayout layoutBackground;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventPoster = itemView.findViewById(R.id.imageView);
            layoutBackground = itemView.findViewById(R.id.event_background);
        }

        public TextView getEventTitleTextView() {
            return eventTitle;
        }

        public TextView getEventDescriptionTextView() {
            return eventDescription;
        }

        public ImageView getEventPoster() {
            return eventPoster;
        }

        public LinearLayout getLayoutBackground() {
            return layoutBackground;
        }


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventViewHolder viewHolder = (EventViewHolder) holder;
        viewHolder.getEventTitleTextView().setText(events.get(position).getName());
        viewHolder.getEventDescriptionTextView().setText(events.get(position).getDescription());

        // Create an instance of DatabaseController
        DatabaseController databaseController = new DatabaseController();

        // Call the getEventPoster method with the event UUID and implement the callback
        // interface
        databaseController.getEventPoster(events.get(position).getUuid(),
                new DatabaseController.EventImageUriCallbacks() {
                    @Override
                    public void onEventPosterCallback(Uri imageUri) {
                        // Handle successful retrieval of the image URI (e.g., load the image into an
                        // ImageView)
                        Picasso.get().load(imageUri).into(viewHolder.getEventPoster());

                    }

                    @Override
                    public void onEventCheckInQRCodeCallback(Uri imageUri) {
                        // to be implemented if required
                    }

                    @Override
                    public void onEventDescriptionQRCodeCallback(Uri imageUri) {
                        // to be implemented if required
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle failure to retrieve the image URI
                        Log.e("EventPoster", "Error getting image URI", e);
                    }

                });

        // Checks to see if the event is one that the user checked into
        checkEvent = events.get(position);
        checkedInUsers = (ArrayList<String>) checkEvent.getCheckedInUsersUUIDs();

        for (int i = 0; i < checkedInUsers.size(); i++) {
            if (checkedInUsers.get(i).equals(userController.getUser().getId())) {
                viewHolder.getEventTitleTextView().setTextColor(Color.WHITE);
                viewHolder.getLayoutBackground().setBackgroundColor(Color.parseColor("#007c41"));
                viewHolder.getEventDescriptionTextView().setTextColor(Color.parseColor("#ffdb05"));
                viewHolder.getEventDescriptionTextView().setText("\nEvent Checked In!");
                viewHolder.getEventDescriptionTextView().setTypeface(null, Typeface.BOLD);
                viewHolder.getEventDescriptionTextView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }

        viewHolder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(events.get(position), position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Event event, int position);
    }

}

public class EventArrayAdapter extends ArrayAdapter<Event> implements DatabaseController.EventImageUriCallbacks {
    DatabaseController databaseController = new DatabaseController();

    private int layoutID;

    private ImageView eventPoster;

    public EventArrayAdapter(Context context, ArrayList<Event> events, OnItemClickListener onItemClickListener) {
        super();
        this.events = events;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        userController = new UserController();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (layoutID == R.layout.admin_event_list_item) {
            view = LayoutInflater.from(context).inflate(layoutID, parent, false);

            Event event = events.get(position);

            TextView eventTitle = view.findViewById(R.id.adminViewEventName);
            TextView organizerID = view.findViewById(R.id.adminViewOrganizerID);
            TextView eventID = view.findViewById(R.id.adminViewEventID);
            TextView eventCapacity = view.findViewById(R.id.adminViewEventCapacity);
            eventPoster = view.findViewById(R.id.adminViewEventPosterImage);
            Button deleteButton = view.findViewById(R.id.adminViewDeleteEvent);

            ImageView deleteImage = view.findViewById(R.id.adminViewEventXButton);

            databaseController.getEventPoster(event.getUuid(), eventPoster, this);

            eventTitle.setText(event.getName());
            organizerID.setText(String.format(context.getString(R.string.organizerid), event.getCreatorUUID()));
            eventID.setText(String.format(context.getString(R.string.eventid), event.getUuid()));
            eventCapacity.setText(String.format(context.getString(R.string.division),
                    event.getSignedUpUsersUUIDs().size(), event.getCapacity()));

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    events.remove(event);
                    notifyDataSetChanged();
                    databaseController.deleteEvent(event);
                }
            });

            deleteImage.setOnClickListener(new View.OnClickListener() {

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
