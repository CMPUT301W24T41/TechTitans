package com.example.eventsigninapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

/**
 * The UserArrayAdapter class allows the list of users to be displayed in a list view.
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private List<User> users;
    private Context context;
    private int layoutID;

    public UserArrayAdapter(Context context, List<User> users) {
        super(context, 0,  users);
        this.users = users;
        this.context = context;
    }
    public UserArrayAdapter(Context context, int layoutID, List<User> users) {
        super(context, layoutID, users);
        this.users = users;
        this.context = context;
        this.layoutID = layoutID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (layoutID == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.attendee_list_item, parent, false);


            Log.e("DEBUG", "User adapter called");

            User user = users.get(position);

            TextView firstName = view.findViewById(R.id.first_name);
            TextView lastName = view.findViewById(R.id.last_name);

            try {
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
            } catch (Exception e) {
                Log.e("DEBUG", String.format("Error: %s", e.getMessage()));
            }
        } else if (layoutID == R.layout.admin_user_list_item) {
            User user = users.get(position);

            view = LayoutInflater.from(context).inflate(R.layout.admin_user_list_item, parent, false);

            TextView firstName = view.findViewById(R.id.adminViewFirstName);
            TextView lastName = view.findViewById(R.id.adminViewLastName);
            TextView userID = view.findViewById(R.id.adminViewID);
            TextView contact = view.findViewById(R.id.adminViewContact);
            ImageView profilePic = view.findViewById(R.id.adminViewProfilePicture);


            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            contact.setText(user.getContact());
            userID.setText(user.getId());
            Picasso.get().load(user.getPicture()).into(profilePic);



        }

        return view;
    }

}
