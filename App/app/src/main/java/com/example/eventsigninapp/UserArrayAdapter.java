package com.example.eventsigninapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserArrayAdapter extends ArrayAdapter<String> {
    private List<String> users;
    private Context context;

    public UserArrayAdapter(Context context, List<String> users) {
        super(context, 0,  users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.attendee_list_item, parent, false);
        }

        String uuid = users.get(position);

        TextView firstName = view.findViewById(R.id.user_first_name);
        TextView lastName = view.findViewById(R.id.user_last_name);

        return view;
    }
}
