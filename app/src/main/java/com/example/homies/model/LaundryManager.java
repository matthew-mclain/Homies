package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import timber.log.Timber;

public class LaundryManager {
    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = LaundryManager.class.getSimpleName();

    public LaundryManager() {
    }

    public LaundryManager(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public static void createLaundryManager(String householdId) {
        LaundryManager laundryManager = new LaundryManager(householdId);

        db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .add(laundryManager)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("Laundry manager created successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating laundry manager");
                });
    }
}
