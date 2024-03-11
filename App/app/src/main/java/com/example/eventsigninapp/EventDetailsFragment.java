package com.example.eventsigninapp;


import static android.content.Intent.getIntent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

    private TextView eventDescription, announcement;
    private ImageView eventPoster;
    private Button backButton;

    /**
     * Used for passing in data through Bundle from
     * Event list fragment. Data passed should be Event Class.
     */
    static EventDetailsFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);

        EventDetailsFragment eventFragment = new EventDetailsFragment();
        eventFragment.setArguments(args);
        return eventFragment;
    }

    /**
     *
     * Called when fragment is created.
     * Setting display details to data passed through Bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eventdetails, container, false);

        // Find views
        eventDescription = view.findViewById(R.id.eventDetails);
        announcement = view.findViewById(R.id.eventAnnouncements);
        eventPoster = view.findViewById(R.id.poster);

        Bundle bundle = getArguments();
        Event event = (Event) bundle.get("event");
        eventDescription.setText(event.getDescription());


        // this is when we want to go back
        backButton = view.findViewById(R.id.btnEventDetails);
        backButton.setOnClickListener(v -> {
            HomeFragment homeFrag = new HomeFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(((ViewGroup) getView().getParent()).getId(), homeFrag).commit();
        });

        return view;
    }



}
