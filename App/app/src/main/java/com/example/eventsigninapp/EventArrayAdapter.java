package com.example.eventsigninapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventArrayAdapter extends RecyclerView.Adapter {
    private ArrayList<Event> events;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private ImageView eventPoster;
        private TextView eventTitle;
        private TextView eventDescription;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventPoster = itemView.findViewById(R.id.imageView);
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


    }

    public EventArrayAdapter(Context context, ArrayList<Event> events, OnItemClickListener onItemClickListener) {
        super();
        this.events = events;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
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
        //viewHolder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(events.get(position), position));
//      Create an instance of DatabaseController
        DatabaseController databaseController = new DatabaseController();

        // Call the getEventPoster method with the event UUID and implement the callback interface
        databaseController.getEventPoster(events.get(position).getUuid(), new DatabaseController.EventImageUriCallbacks() {
            @Override
            public void onEventPosterCallback(Uri imageUri) {
                // Handle successful retrieval of the image URI (e.g., load the image into an ImageView)
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

        }
        );
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
