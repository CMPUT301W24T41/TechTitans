package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class MyEventsFragment extends Fragment implements DatabaseController.GetAllEventsCallback, EventArrayAdapter.OnItemClickListener, DatabaseController.GetCheckedInUsersCallback {
    UserController userController;
    DatabaseController dbController;
    ArrayList<Event> myEventsArrayList;
    EventArrayAdapter myEventsArrayAdapter;
    MyEventsView myEventsView;
    EventDetailsFragment frag;
    Collection<String> checkedInUsers;
    ArrayList<String> signedUpEvents;

    public MyEventsFragment() {
        userController = new UserController();
        dbController = new DatabaseController();

        myEventsArrayList = new ArrayList<>();
        checkedInUsers = new ArrayList<>();
        signedUpEvents = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEventsArrayList, this);
        dbController.getAllEventsFromFirestore(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myEventsView = new MyEventsView(inflater, container);

        myEventsView.setMyEventsListArrayAdapter(myEventsArrayAdapter);

        return myEventsView.getRootView();
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
     * This function adds new events to myEventsArrayList.
     * @param newEventsArrayList The list of events to be added to the list of events.
     */
    private void addNewEventsToArrayLists(ArrayList<Event> newEventsArrayList) {
         newEventsArrayList.forEach(newEvent -> {

            dbController.getCheckedInUsersFromFirestore(newEvent, this);

            signedUpEvents = userController.getUser().getAttendingEvents();
            for (int i=0; i<signedUpEvents.size(); i++) {
                if (signedUpEvents.get(i).equals(newEvent.getUuid())) {
                    myEventsArrayList.add(newEvent);
                    myEventsArrayAdapter.notifyItemInserted(myEventsArrayList.indexOf(newEvent));
                }
            }

        });
    }

    @Override
    public void onGetCheckedInUsersCallback(Event event, ArrayList<?> users) {
        Log.e("DEBUG", "callback called!");
        try {
            for (int i = 0; i < users.size(); i++) {
                event.addCheckedInUser((String) users.get(i));

                // If checked in event is already displayed by signed up event
                for (int j=0; j<myEventsArrayList.size(); j++) {
                    if (myEventsArrayList.get(j).equals(event)) {
                        return;
                    }
                }

                // If Event is checked in, add to start of list, top of display
                if (users.get(i).equals(userController.getUser().getId())) {
                    myEventsArrayList.add(0, event);
                    myEventsArrayAdapter.notifyItemInserted(myEventsArrayList.indexOf(event));
                    }
                dbController.getUserFromFirestore((String) users.get(i), new DatabaseController.UserCallback() {
                    @Override
                    public void onCallback(User user) {
                        checkedInUsers.add(user.getId());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("DEBUG", String.format("Error getting checked in users: %s", e.getMessage()));
                    }
                });
            }
        } catch (Exception e) {
            Log.e("DEBUG", String.format("Error: %s", e.getMessage()));
        }
    }

    /**
     * This function removes old events from the list of events.
     * @param newEventsArrayList The list of events to be added to the list of events.
     */
    private void purgeOldEventsFromArrayLists(ArrayList<Event> newEventsArrayList) {
        // Create a copy of allEventsArrayList to avoid ConcurrentModificationException
        ArrayList<Event> myEventsArrayListCopy = new ArrayList<>(myEventsArrayList);

        // Remove old events that are not in the new list or have been updated
        myEventsArrayListCopy.stream()
                .filter(oldEvent -> newEventsArrayList.stream().noneMatch(newEvent -> newEvent.equals(oldEvent) || newEvent.getUuid().equals(oldEvent.getUuid())))
                .forEach(oldEvent -> {
                    int myEventsIndex = myEventsArrayList.indexOf(oldEvent);
                    myEventsArrayList.remove(oldEvent);
                    myEventsArrayAdapter.notifyItemRemoved(myEventsIndex);
                });

    }

    @Override
    public void onItemClick(Event event, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        frag = new EventDetailsFragment();
        frag.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null)
                .commit();
    }

}
