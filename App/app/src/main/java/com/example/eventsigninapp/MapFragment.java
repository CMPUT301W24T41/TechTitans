package com.example.eventsigninapp;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, DatabaseController.GetCheckInLocationCallback {
    Button backButton;
    TextView eventTitle;
    DatabaseController dbController;
    ArrayList<Location> locations;
    Event event;
    GoogleMap map;
    final int ZOOM_LEVEL = 15;


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
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        backButton = view.findViewById(R.id.back_button);
        eventTitle = view.findViewById(R.id.event_title);

        dbController = new DatabaseController();
        locations = new ArrayList<>();

        eventTitle.setText(event.getName());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        dbController.getCheckInLocationsFromFirestore(event, this);

        backButton.setOnClickListener(l -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            CheckedInUsersFragment oldFrag = new CheckedInUsersFragment();
            oldFrag.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, oldFrag)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void addMarkersToMap() {
        LatLng center = getAvgLatLng();
        for (Location loc : locations) {
            LatLng point = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.addMarker(new MarkerOptions().position(point));
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, ZOOM_LEVEL));
    }

    private LatLng getAvgLatLng() {
        double totalLat = 0, totalLng = 0;
        for (Location loc : locations) {
            totalLat += loc.getLatitude();
            totalLng += loc.getLongitude();
        }

        if (totalLat == 0 || totalLng == 0) {
            return new LatLng(0, 0);
        }

        return new LatLng(totalLat / locations.size(), totalLng / locations.size());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onGetCheckInLocationCallback(Event event, ArrayList<?> checkInLocations) {
        for (int i = 0; i < checkInLocations.size(); i++) {
            GeoPoint gPoint = (GeoPoint) checkInLocations.get(i);
            Location loc = new Location("prov");
            loc.setLatitude(gPoint.getLatitude());
            loc.setLongitude(gPoint.getLongitude());
            locations.add(loc);
        }
        addMarkersToMap();
    }

}
