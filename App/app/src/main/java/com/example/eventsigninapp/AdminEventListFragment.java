package com.example.eventsigninapp;

// Import statements

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

import java.util.ArrayList;

public class AdminEventListFragment extends Fragment implements DatabaseController.GetAllEventsCallback {

    // Member variables
    DatabaseController databaseController = new DatabaseController();
    ArrayList<Event> events;
    ListView eventList;
    AdminEventArrayAdapter eventsArrayAdapter;
    TextView title;
    EventDetailsFragment frag;

    // Default constructor
    public AdminEventListFragment(){};

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
        title = view.findViewById(R.id.adminListTitle);
        eventList = view.findViewById(R.id.adminList);

        // Set title
        title.setText(R.string.eventListTitle);

        // Initialize events list
        events = new ArrayList<Event>();

        // Initialize and set adapter for event list view
        eventsArrayAdapter = new AdminEventArrayAdapter(getContext(), R.layout.admin_event_list_item, events);
        eventList.setAdapter(eventsArrayAdapter);

        // Retrieve events from Firestore database
        databaseController.getAllEventsFromFirestore(this);
        eventsArrayAdapter.notifyDataSetChanged();

        //
        //TODO potentially, Set item click listener for event list view
        /*eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("DEBUG", "item clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", events.get(position));
                frag = new EventDetailsFragment();
                frag.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
            }
        });*/

        return view;
    }

    // Callback method for getting all events from Firestore database
    @Override
    public void onGetAllEventsCallback(ArrayList<Event> allEvents) {
        // Clear the current list of events and add all retrieved events
        events.clear();
        events.addAll(allEvents);
        // Notify the adapter that the dataset has changed
        eventsArrayAdapter.notifyDataSetChanged();
    }
}
