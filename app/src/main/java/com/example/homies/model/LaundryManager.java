package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import timber.log.Timber;

public class LaundryManager {
    private String householdId;
    private String laundryManagerId;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();

    public LaundryManager(String householdId) {
        this.householdId = householdId;
    }

    public String getLaundryManagerId() {
        return laundryManagerId;
    }

    public void setLaundryManagerId(String laundryManagerId) {
        this.laundryManagerId = laundryManagerId;
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
                    // Laundry manager created successfully, obtain its ID
                    String laundryManagerId = documentReference.getId();
                    laundryManager.setLaundryManagerId(laundryManagerId);
                    Timber.tag(TAG).d("Laundry manager created successfully: %s", laundryManagerId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating laundry manager");
                });
    }
}
