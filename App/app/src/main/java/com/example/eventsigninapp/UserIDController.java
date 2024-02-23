package com.example.eventsigninapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * This class should help control and fetch the users unique UID to later use to find the UID
 */
public class UserIDController {

    private static final String uuidKey = "UUID_KEY";
    private static final String prefName = "ID";

    public UserIDController(){}

    /**
     * Gets the user's UUID if it exists, otherwise generates a new one and saves it
     * by using a shared preference from the context provided
     * @param context: the target context to pull the shared preference from
     * @return A string representing the User's UUID
     */
    public String getUserID(Context context){
        //Gets a Shared Preference for correctly logging in the user
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String uuidString = preferences.getString(uuidKey, null);

        //generates a uuid if the user does not have a uuid
        if (uuidString == null) {
            uuidString = UUID.randomUUID().toString();
            saveUUID(context, uuidString);
        }

        //returns the uuid of the user
        return uuidString;
    }

    /**
     * Updates the users UUID in the shared preference of the provided context
     * @param context: the target context
     * @param uuid: the new string value of the uuid
     */
    public void saveUUID(Context context, String uuid) {
        SharedPreferences preferences = context.getSharedPreferences("ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(uuidKey, uuid);
        editor.apply();
    }
}


