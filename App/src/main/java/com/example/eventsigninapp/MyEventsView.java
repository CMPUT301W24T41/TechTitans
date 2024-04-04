package com.example.eventsigninapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyEventsView {
    private RecyclerView myEventsListView;
    private final View rootView;

    public MyEventsView(LayoutInflater inflater, ViewGroup parent) {
        rootView = inflater.inflate(R.layout.fragment_my_events, parent, false);
        myEventsListView = rootView.findViewById(R.id.allMyEventsList);
        myEventsListView.setLayoutManager(new LinearLayoutManager(parent.getContext(), LinearLayoutManager.VERTICAL, false));

    }

    public void setMyEventsListArrayAdapter(EventArrayAdapter adapter) {
        myEventsListView.setAdapter(adapter);
    }

    public View getRootView() {
        return rootView;
    }
}
