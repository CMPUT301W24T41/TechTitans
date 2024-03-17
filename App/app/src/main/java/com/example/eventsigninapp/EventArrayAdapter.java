package com.example.eventsigninapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventArrayAdapter extends RecyclerView.Adapter {
    private ArrayList<Event> events;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView eventTitle;
        private TextView eventDescription;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
        }

        public TextView getEventTitleTextView() {
            return eventTitle;
        }

        public TextView getEventDescriptionTextView() {
            return eventDescription;
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
