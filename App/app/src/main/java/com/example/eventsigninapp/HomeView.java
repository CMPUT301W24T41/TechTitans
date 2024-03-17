package com.example.eventsigninapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeView {
    private RecyclerView allEventsListView;
    private RecyclerView myEventsListView;

    private final View rootView;

    public HomeView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_home, parent, false);

        allEventsListView = rootView.findViewById(R.id.all_events_list);
        myEventsListView = rootView.findViewById(R.id.my_events_list);

        allEventsListView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false));
        myEventsListView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void setAllEventsListArrayAdapter(EventArrayAdapter adapter) {
        allEventsListView.setAdapter(adapter);
    }

    public void setMyEventsListArrayAdapter(EventArrayAdapter adapter) {
        myEventsListView.setAdapter(adapter);
    }

    public View getRootView() {
        return rootView;
    }
}
