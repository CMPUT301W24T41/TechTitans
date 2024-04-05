package com.example.eventsigninapp;

// Import statements

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class AdminUserListFragment extends Fragment implements DatabaseController.GetAllUsersCallback {

    // Member variables
    DatabaseController databaseController = new DatabaseController();
    ArrayList<User> users;
    UserArrayAdapter userArrayAdapter;

    // Default constructor
    public AdminUserListFragment(){};

    // onCreate method
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // onCreateView method
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_list, container, false);

        // Find views
        TextView title = view.findViewById(R.id.adminListTitle);
        ListView userList = view.findViewById(R.id.adminList);

        // Set title
        title.setText(R.string.userListTitle);

        // Initialize users list and adapter for list view
        users = new ArrayList<User>();
        userArrayAdapter  = new UserArrayAdapter(getContext(), R.layout.admin_user_list_item, users);
        userList.setAdapter(userArrayAdapter);

        // Retrieve all users from Firestore database
        databaseController.getAllUsersFromFirestore(this);

        // TODO: maybe add an expanded view of someone's profile
        /*userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("DEBUG", "item clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", users.get(position));
                frag = new Fragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
            }
        });*/

        return view;
    }

    // Callback method for getting all users from Firestore database
    @Override
    public void onGetAllUserCallback(User user) {
        // Add the retrieved user to the list and notify the adapter
        users.add(user);
        userArrayAdapter.notifyDataSetChanged();
    }
}
