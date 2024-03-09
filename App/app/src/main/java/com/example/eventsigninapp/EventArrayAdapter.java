package com.example.eventsigninapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;


    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }

        Event event = events.get(position);
        Log.e("DEBUG", String.format("Looking at %s", event.getName()));

        TextView eventTitle = view.findViewById(R.id.event_title);
        TextView eventDescription = view.findViewById(R.id.event_description);

        eventTitle.setText(event.getName());
        eventDescription.setText(event.getDescription());
        return view;
    }
}
