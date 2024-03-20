package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminUserListFragment extends Fragment implements DatabaseController.GetAllUserCallback {

    DatabaseController databaseController = new DatabaseController();

    ArrayList<User> users;
    UserArrayAdapter userArrayAdapter;

    public AdminUserListFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_list, container, false);

        TextView title = view.findViewById(R.id.adminListTitle);
        ListView userList = view.findViewById(R.id.adminList);

        title.setText(R.string.userListTitle);

        users = new ArrayList<User>();

        userArrayAdapter  = new UserArrayAdapter(getContext(), R.layout.admin_user_list_item, users);
        userList.setAdapter(userArrayAdapter);


        databaseController.getAllUsersFromFirestore(this);
        userArrayAdapter.notifyDataSetChanged();


        // TODO: maybe add a expanded view of someones profile
//        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Log.e("DEBUG", "item clicked");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("user", users.get(position));
//                 frag = new Fragment();
//
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
//            }
//        });


        return view;
    }





    @Override
    public void onGetAllUserCallback(User user) {
        users.add(user);
        userArrayAdapter.notifyDataSetChanged();
    }

}
