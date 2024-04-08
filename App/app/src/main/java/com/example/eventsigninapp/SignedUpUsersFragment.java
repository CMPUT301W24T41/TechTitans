package com.example.eventsigninapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class SignedUpUsersFragment extends Fragment implements DatabaseController.GetSignedUpUsersCallback {
    private DatabaseController dbController;
    private UserArrayAdapter userAdapter;
    private TextView signedUpCount;
    private ListView signedUpList;
    private Button backButton;
    private ArrayList<User> signedUpUsers;
    private Event event;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.e("SIGNUP", "Unpacking " + String.valueOf(bundle.getSerializable("event")));
            event = (Event) bundle.getSerializable("event");
        } else {
            Log.e("DEBUG", "Bundle is null");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signed_up_users, container, false);

        signedUpCount = view.findViewById(R.id.signed_up_users_count);
        signedUpList = view.findViewById(R.id.signed_up_list);
        backButton = view.findViewById(R.id.back_button);

        signedUpUsers = new ArrayList<>();
        userAdapter = new UserArrayAdapter(requireContext(), signedUpUsers, event);
        dbController = new DatabaseController();

        signedUpList.setAdapter(userAdapter);

        dbController.getSignedUpUsersFromFirestore(event, this);

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

        return view;
    }

    @Override
    public void onGetSignedUpUsersCallback(Event event, ArrayList<?> users) {
        signedUpCount.setText(String.format(Locale.CANADA, "There are %d attendees signed up to this event.", users.size()));
        for (int i = 0; i < users.size(); i++) {
            event.addSignedUpUser((String) users.get(i));

            int finalI = i;
            dbController.getUserFromFirestore((String) users.get(i), new DatabaseController.UserCallback() {
                @Override
                public void onCallback(User user) { // user has a profile
                    signedUpUsers.add(user);
                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) { // user has no profile
                    User user = new User((String) users.get(finalI));
                    signedUpUsers.add(user);
                    userAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
