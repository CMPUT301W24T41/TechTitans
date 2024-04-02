package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminEventListFragment extends Fragment implements DatabaseController.GetAllEventsCallback {

    DatabaseController databaseController = new DatabaseController();

    ArrayList<Event> events;

    ListView eventList;
    EventArrayAdapter eventsArrayAdapter;

    TextView title;
    EventDetailsFragment frag;


    public AdminEventListFragment(){};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_list, container, false);

         title = view.findViewById(R.id.adminListTitle);
         eventList = view.findViewById(R.id.adminList);

        title.setText(R.string.eventListTitle);

        events = new ArrayList<Event>();

        eventsArrayAdapter = new EventArrayAdapter(getContext(), R.layout.admin_event_list_item, events);
        eventList.setAdapter(eventsArrayAdapter);


        databaseController.getAllEventsFromFirestore(this);
        eventsArrayAdapter.notifyDataSetChanged();



        //TODO fix this does not work
//        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Log.e("DEBUG", "item clicked");
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("event", events.get(position));
//                frag = new EventDetailsFragment();
//                frag.setArguments(bundle);
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
//            }
//        });




        return view;
    }

    @Override
    public void onGetAllEventsCallback(Event event) {
        events.add(event);
        eventsArrayAdapter.notifyDataSetChanged();
    }


}
