package com.example.eventsigninapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {
    DatabaseController databaseController = new DatabaseController();
    UserController userController = new UserController();


    private TextView eventDescription, announcement;
    private ImageView eventPoster;
    private Button backButton, editEventButton, notifyUsersButton;

    /**
     * Used for passing in data through Bundle from
     * Event list fragment. Data passed should be Event Class.
     */
    static EventDetailsFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putSerializable("event", event);
        EventController eventController = new EventController(event);

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
        editEventButton = view.findViewById(R.id.editEventButton);
        notifyUsersButton = view.findViewById(R.id.notifyUsersButton);
        backButton = view.findViewById(R.id.btnEventDetails);

        Bundle bundle = getArguments();
        Event event = (Event) bundle.get("event");
        eventDescription.setText(event.getDescription());

        if(userController.getUser().getId().equals(event.getCreatorUUID())){
            editEventButton.setVisibility(View.VISIBLE);
            notifyUsersButton.setVisibility(View.VISIBLE);

        }
        else{
            editEventButton.setVisibility(View.GONE);
            notifyUsersButton.setVisibility(View.GONE);
        }

        notifyUsersButton.setOnClickListener(v -> {
            NotifyUsersBottomSheetFragment bottomSheetFragment = new NotifyUsersBottomSheetFragment();
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });









        backButton.setOnClickListener(v -> {
            HomeFragment homeFrag = new HomeFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(((ViewGroup) getView().getParent()).getId(), homeFrag).commit();
        });

        return view;
    }



}
