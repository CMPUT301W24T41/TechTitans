package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.internal.annotations.EverythingIsNonNull;

public class CheckedInUsersFragment extends Fragment implements DatabaseController.GetCheckedInUsersCallback, DatabaseController.GetCheckedInUserCountCallback {
    private DatabaseController dbController;
    private UserArrayAdapter userAdapter;
    private TextView checkedInCount;
    private ListView checkedInList;
    private Button backButton;
    private Button mapButton;
    private ArrayList<User> checkedInUsers;
    private Event event;
    private Integer count;
    private int firstMilestone;
    private int secondMilestone;
    private int thirdMilestone;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            event = (Event) bundle.getSerializable("event");
        } else {
            Log.e("DEBUG", "Bundle is null");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checked_in_users, container, false);

        checkedInCount = view.findViewById(R.id.checked_in_users_count);
        checkedInList = view.findViewById(R.id.checked_in_list);
        backButton = view.findViewById(R.id.back_button);
        mapButton = view.findViewById(R.id.button_to_map);

        checkedInUsers = new ArrayList<>();
        userAdapter = new UserArrayAdapter(requireContext(), checkedInUsers, event);
        dbController = new DatabaseController();

        checkedInList.setAdapter(userAdapter);

        dbController.getCheckedInUsersFromFirestore(event, this);
        dbController.getCheckedInUserCountFromFirestore(event, this);

        backButton.setOnClickListener(l -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            EventDetailsFragment oldFrag = new EventDetailsFragment();
            oldFrag.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, oldFrag)
                    .addToBackStack(null)
                    .commit();
        });

        mapButton.setOnClickListener(l -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            MapFragment newFrag = new MapFragment();
            newFrag.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, newFrag)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onGetCheckedInUsersCallback(Event event, ArrayList<?> users) {
        checkedInCount.setText(String.format(Locale.CANADA, "There are %d attendees checked into your event.", users.size()));

        for (int i = 0; i < users.size(); i++) {
            event.addCheckedInUser((String) users.get(i));

            int finalI = i;
            dbController.getUserFromFirestore((String) users.get(i), new DatabaseController.UserCallback() {
                @Override
                public void onCallback(User user) { // user has profile
                    checkedInUsers.add(user);
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) { // user has no profile
                    User user = new User((String) users.get(finalI));
                    checkedInUsers.add(user);
                    userAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public void onGetCheckedInUserCountCallback(Event event, HashMap<?,?> users) {
        try {
            HashMap<String, Long> checkedInUsersCount = (HashMap<String, Long>) users;
            for (String uuid : checkedInUsersCount.keySet()) {
                Long value = checkedInUsersCount.get(uuid);
                count = (int) (long) value;
                event.addCheckedInCount(uuid, count);
            }
            checkMilestones();

        } catch (Exception e) {
            Log.e("DEBUG", String.format("Error getting checked in user count: %s", e.getMessage()));
        }
    }

    private void checkMilestones() {
        if (!event.isCapped()) {
            firstMilestone = 50;
            secondMilestone = 150;
            thirdMilestone = 300;
        } else {
            int cap = event.getCapacity();
            firstMilestone = (int) Math.round(cap * 0.3);
            secondMilestone = (int) Math.round(cap * 0.7);
            thirdMilestone = (int) Math.round(cap * 0.9);
        }

        if (count >= firstMilestone && count < secondMilestone) {
            Toast.makeText(getContext(), String.format(Locale.CANADA, "Congratulations! %d attendees are checked in! (your first milestone)", count), Toast.LENGTH_LONG).show();
        } else if (count >= secondMilestone && count < thirdMilestone) {
            Toast.makeText(getContext(), String.format(Locale.CANADA, "Congratulations! %d attendees are checked in! (your second milestone)", count), Toast.LENGTH_LONG).show();
        } else if (count >= thirdMilestone) {
            Toast.makeText(getContext(), String.format(Locale.CANADA, "Congratulations! %d attendees are checked in! (your third milestone)", count), Toast.LENGTH_LONG).show();
        }
    }

}
