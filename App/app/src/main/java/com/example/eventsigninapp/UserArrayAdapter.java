package com.example.eventsigninapp;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

/**
 * The UserArrayAdapter class allows the list of users to be displayed in a list view.
 */
public class UserArrayAdapter extends ArrayAdapter<User> implements DatabaseController.ImageUriCallback{
    private List<User> users;
    private Context context;
    private int layoutID;


    private UserController userController = new UserController();

    private DatabaseController databaseController = new DatabaseController();

    private ImageView profilePic;

    private Map<String, ImageView> imageViewMap = new HashMap<>();

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

            view = LayoutInflater.from(context).inflate(layoutID, parent, false);

            TextView firstName = view.findViewById(R.id.adminViewFirstName);
            TextView lastName = view.findViewById(R.id.adminViewLastName);
            TextView userID = view.findViewById(R.id.adminViewID);
            TextView contact = view.findViewById(R.id.adminViewContact);
            Button deleteButton = view.findViewById(R.id.adminViewDeleteUser);
            ImageView xButton = view.findViewById(R.id.adminViewUserXButton);


            ImageView profilePic = view.findViewById(R.id.adminViewProfilePictureImage);
            // Set tag with user ID
            profilePic.setTag(user.getId());
            // Add to map for association
            imageViewMap.put(user.getId(), profilePic);

            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            contact.setText(user.getContact());
            userID.setText(user.getId());


            databaseController.getUserProfilePicture(user.getId(), this);

            if(Objects.equals(user.getId(), userController.getUser().getId())) {
                deleteButton.setVisibility(View.INVISIBLE);
            }
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    users.remove(user);
                    notifyDataSetChanged();
                    databaseController.deleteUser(user);
                }
            });


            xButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    users.get(position).deletePicture();
                    databaseController.deleteProfilePicture(user);
                    notifyDataSetChanged();
                }
            });


//            Picasso.get().load(user.getPicture()).into(profilePic);



        }

        return view;
    }

    public void onImageUriCallback(Uri uri) {
        for (Map.Entry<String, ImageView> entry : imageViewMap.entrySet()) {
            String userId = entry.getKey();
            ImageView imageView = entry.getValue();
            if (imageView != null && userId != null && uri != null) {
                // If the tag of the ImageView matches the user ID, load the image
                if (userId.equals(imageView.getTag())) {
                    // Load the image into the correct ImageView
                    Picasso.get().load(uri.toString()).into(imageView);
                    // Break the loop after finding the correct ImageView
                    break;
                }
            }
        }
    }

    @Override
    public void onError(Exception e) {
        // load dummy picture
//        Picasso.get().load(R.drawable.user).into(profilePic);
    }

}
