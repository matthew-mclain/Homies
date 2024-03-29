package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class Calendar {
    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = Calendar.class.getSimpleName();

    public Calendar() {
    }

    public Calendar(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public static void createCalendar(String householdId) {
        Calendar calendar = new Calendar(householdId);
        db = MyApplication.getDbInstance();
        db.collection("calendars")
                .add(calendar)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("Calendar created successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating calendar");
                });
    }
}
