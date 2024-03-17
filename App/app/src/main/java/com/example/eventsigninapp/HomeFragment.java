package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements DatabaseController.GetAllEventsCallback, EventArrayAdapter.OnItemClickListener {
    UserController userController = new UserController();
    DatabaseController dbController;
    ArrayList<Event> allEvents;
    RecyclerView allEventsList;
    EventArrayAdapter allEventsArrayAdapter;
    ArrayList<Event> myEvents;
    RecyclerView myEventsList;
    EventArrayAdapter myEventsArrayAdapter;

    EventDetailsFragment frag;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allEvents = new ArrayList<>();
        myEvents = new ArrayList<>();
        dbController = new DatabaseController();
        dbController.getAllEventsFromFirestore(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        allEventsList = rootView.findViewById(R.id.all_events_list);
        myEventsList = rootView.findViewById(R.id.my_events_list);

        allEventsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        myEventsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        allEventsArrayAdapter = new EventArrayAdapter(getContext(), allEvents, this);
        allEventsList.setAdapter(allEventsArrayAdapter);

        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEvents, this);
        myEventsList.setAdapter(myEventsArrayAdapter);

        dbController.getAllEventsFromFirestore(this);
        allEventsArrayAdapter.notifyDataSetChanged();

        return rootView;
    }

    /**
     * This function allows an event to be retrieved from the database and added to the list of events.
     * @param event an event
     */
    @Override
    public void onGetAllEventsCallback(Event event) {
        if (!allEvents.contains(event)) {
            allEvents.add(event);

            if (allEventsArrayAdapter != null) {
                allEventsArrayAdapter.notifyItemInserted(-1);
            }
        }

        if (!myEvents.contains(event) && userController.getUser().getId().equals(event.getCreatorUUID())) {
            myEvents.add(event);

            if (myEventsArrayAdapter != null) {
                myEventsArrayAdapter.notifyItemInserted(-1);
            }
        }

        updateAdapters();
    }

    private void updateAdapters() {
        if (allEventsArrayAdapter == null || myEventsArrayAdapter == null) {
            return;
        }

        allEventsArrayAdapter.notifyDataSetChanged();
        myEventsArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Event event, int position) {
        Log.e("DEBUG", "item clicked");
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        frag = new EventDetailsFragment();
        frag.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
    }
}