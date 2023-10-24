package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import timber.log.Timber;

public class Calendar {
    private String householdId;
    private String calendarId;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();

    public Calendar(String householdId) {
        this.householdId = householdId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public interface OnComponentCreatedListener {
        void onComponentCreated(String componentId);
        void onComponentCreationFailed(Exception e);
    }

    public static void createCalendar(String householdId, Calendar.OnComponentCreatedListener listener) {
        Calendar calendar = new Calendar(householdId);

        db = MyApplication.getDbInstance();
        db.collection("calendars")
                .add(calendar)
                .addOnSuccessListener(documentReference -> {
                    // Calendar created successfully, obtain its ID
                    String calendarId = documentReference.getId();
                    calendar.setCalendarId(calendarId);
                    Timber.tag(TAG).d("Calendar created successfully: %s", calendarId);

                    // Invoke the listener to notify that the group chat has been created,
                    // and pass the generated calendarId.
                    listener.onComponentCreated(calendarId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating calendar");

                    // Invoke the listener to notify about the failure.
                    listener.onComponentCreationFailed(e);
                });
    }
}
