package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * This class acts as a controller for the home page.
 * It is responsible for displaying the list of events and handling the user's interaction with the events.
 */
public class HomeFragment extends Fragment implements DatabaseController.GetAllEventsCallback, EventArrayAdapter.OnItemClickListener {
    UserController userController;
    DatabaseController dbController;
    ArrayList<Event> allEventsArrayList;
    EventArrayAdapter allEventsArrayAdapter;
    ArrayList<Event> myEventsArrayList;
    EventArrayAdapter myEventsArrayAdapter;
    HomeView homeView;
    EventDetailsFragment frag;

    public HomeFragment() {
        userController = new UserController();
        dbController = new DatabaseController();

        allEventsArrayList = new ArrayList<>();
        myEventsArrayList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allEventsArrayAdapter = new EventArrayAdapter(getContext(), allEventsArrayList, this);
        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEventsArrayList, this);

        dbController.getAllEventsFromFirestore(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = new HomeView(inflater, container);

        homeView.setAllEventsListArrayAdapter(allEventsArrayAdapter);
        homeView.setMyEventsListArrayAdapter(myEventsArrayAdapter);


        return homeView.getRootView();
    }

    /**
     * This function allows an event to be retrieved from the database and added to the list of events.
     * @param newEventsArrayList The list of events to be added to the list of events.
     */
    @Override
    public void onGetAllEventsCallback(ArrayList<Event> newEventsArrayList) {
        purgeOldEventsFromArrayLists(newEventsArrayList);
        addNewEventsToArrayLists(newEventsArrayList);
    }

    /**
     * This function adds new events to allEventsArrayList and myEventsArrayList.
     * @param newEventsArrayList The list of events to be added to the list of events.
     */
    private void addNewEventsToArrayLists(ArrayList<Event> newEventsArrayList) {
        newEventsArrayList.forEach(newEvent -> {
            allEventsArrayList.add(newEvent);
            allEventsArrayAdapter.notifyItemInserted(allEventsArrayList.indexOf(newEvent));

            if (newEvent.getCreatorUUID().equals(userController.getUser().getId()) && !myEventsArrayList.contains(newEvent)) {
                myEventsArrayList.add(newEvent);
                myEventsArrayAdapter.notifyItemInserted(myEventsArrayList.indexOf(newEvent));
            }
        });
    }

    /**
     * This function removes old events from the list of events.
     * @param newEventsArrayList The list of events to be added to the list of events.
     */
    private void purgeOldEventsFromArrayLists(ArrayList<Event> newEventsArrayList) {
        // Create a copy of allEventsArrayList to avoid ConcurrentModificationException
        ArrayList<Event> allEventsArrayListCopy = new ArrayList<>(allEventsArrayList);

        // Remove old events that are not in the new list or have been updated
        allEventsArrayListCopy.stream()
                .filter(oldEvent -> newEventsArrayList.stream().noneMatch(newEvent -> newEvent.equals(oldEvent) || newEvent.getUuid().equals(oldEvent.getUuid())))
                .forEach(oldEvent -> {
                    int _allEventsIndex = allEventsArrayList.indexOf(oldEvent);
                    allEventsArrayList.remove(oldEvent);
                    allEventsArrayAdapter.notifyItemRemoved(_allEventsIndex);
                });

        // Remove events that are in myEventsArrayList but not in allEventsArrayList
        myEventsArrayList.stream()
                .filter(oldEvent -> !allEventsArrayList.contains(oldEvent))
                .forEach(oldEvent -> {
                    int _myEventsIndex = myEventsArrayList.indexOf(oldEvent);
                    myEventsArrayList.remove(oldEvent);
                    myEventsArrayAdapter.notifyItemRemoved(_myEventsIndex);
                });
    }

    @Override
    public void onItemClick(Event event, int position) {
        Log.d("HomeFragment", "Event Clicked: " + event.getSignedUpUsersUUIDs());
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        frag = new EventDetailsFragment();
        frag.setArguments(bundle);
        Log.d("HomeFragment", "Event Signed Up Users: " + event.getSignedUpUsersUUIDs());

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null)
                .commit();
    }
}