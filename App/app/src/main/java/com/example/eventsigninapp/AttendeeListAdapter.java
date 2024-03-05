package com.example.eventsigninapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.ViewHolder> {

    private List<User> attendeeList;

    public AttendeeListAdapter(List<User> attendeeList) {
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User attendee = attendeeList.get(position);
        holder.bindData(attendee);
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView attendeeNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setReferences(itemView);
        }

        private void setReferences(View itemView) {
            attendeeNameTextView = itemView.findViewById(R.id.attendeeNameTextView);
        }

        public void bindData(User attendee) {
            // Bind data to views
            String fullName = attendee.getFirstName() + " " + attendee.getLastName();
            attendeeNameTextView.setText(fullName);
        }
    }
}

