package com.example.eventsigninapp;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDatabaseController {
    private FirebaseFirestore db;

    public EventDatabaseController() {
        db = FirebaseFirestore.getInstance();
    }

    public void pushEventToFirestore(Event event) {
        DocumentReference eventRef = db.collection("events").document(event.getUuid());
        eventRef.set(event.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Database", "pushEventToFirestore: Event data successfully updated");
                })
                .addOnFailureListener(e -> {
                    Log.e("Database", "pushEventToFirestore: Error updating event data", e);
                });
    }

    private void updateEventInFirestore(Event event) {
        db.collection("events")
                .document(event.getUuid())
                .update(event.toMap());
    }

    private void createEventInFirestore(Event event) {
        db.collection("events")
                .document(event.getUuid())
                .set(event.toMap());
    }

    public void getEventFromFirestore(String uuid, GetEventCallback callback) {
        DocumentReference eventRef = db.collection("events").document(uuid);

        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    callback.onGetEventCallback(document.toObject(Event.class), uuid);
                } else {
                    callback.onGetEventCallback(null, null);
                }
            }
        });
    }

    public interface GetEventCallback {
        void onGetEventCallback(Event event, String uuid);
    }
}