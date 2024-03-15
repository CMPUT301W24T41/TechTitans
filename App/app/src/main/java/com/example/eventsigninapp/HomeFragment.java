package com.example.eventsigninapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements DatabaseController.GetAllEventsCallback {
    UserController userController = new UserController();
    DatabaseController dbController;
    ArrayList<Event> allEvents;
    ListView allEventsList;
    EventArrayAdapter allEventsArrayAdapter;
    ArrayList<Event> myEvents;
    ListView myEventsList;
    EventArrayAdapter myEventsArrayAdapter;

    EventDetailsFragment frag;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        allEventsList = rootView.findViewById(R.id.all_events_list);
        myEventsList = rootView.findViewById(R.id.my_events_list);

        dbController = new DatabaseController();
        allEvents = new ArrayList<Event>();
        myEvents = new ArrayList<Event>();

        allEventsArrayAdapter = new EventArrayAdapter(getContext(), allEvents);
        allEventsList.setAdapter(allEventsArrayAdapter);

        myEventsArrayAdapter = new EventArrayAdapter(getContext(), myEvents);
        myEventsList.setAdapter(myEventsArrayAdapter);

        dbController.getAllEventsFromFirestore(this);
        allEventsArrayAdapter.notifyDataSetChanged();


        allEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("DEBUG", "item clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", allEvents.get(position));
                frag = new EventDetailsFragment();
                frag.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
            }
        });

        myEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("DEBUG", "item clicked");
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", myEvents.get(position));
                frag = new EventDetailsFragment();
                frag.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) getView().getParent()).getId(), frag).commit();
            }
        });

        return rootView;
    }

    /**
     * This function allows an event to be retrieved from the database and added to the list of events.
     * @param event an event
     */
    @Override
    public void onGetAllEventsCallback(Event event) {
        allEvents.add(event);
        if(userController.getUser().getId().equals(event.getCreatorUUID())){
            myEvents.add(event);
        }
        allEventsArrayAdapter.notifyDataSetChanged();
        myEventsArrayAdapter.notifyDataSetChanged();
    }
}