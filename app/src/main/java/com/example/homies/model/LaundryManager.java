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

    public void setLaundryManagerId(String laundryManagerId) {
        this.laundryManagerId = laundryManagerId;
    }

    public interface OnComponentCreatedListener {
        void onComponentCreated(String componentId);
        void onComponentCreationFailed(Exception e);
    }

    public static void createLaundryManager(String householdId, OnComponentCreatedListener listener) {
        LaundryManager laundryManager = new LaundryManager(householdId);

        db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .add(laundryManager)
                .addOnSuccessListener(documentReference -> {
                    // Laundry manager created successfully, obtain its ID
                    String laundryManagerId = documentReference.getId();
                    laundryManager.setLaundryManagerId(laundryManagerId);
                    Timber.tag(TAG).d("Laundry manager created successfully: %s", laundryManagerId);

                    // Invoke the listener to notify that the group chat has been created,
                    // and pass the generated laundryManagerId.
                    listener.onComponentCreated(laundryManagerId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating laundry manager");

                    // Invoke the listener to notify about the failure.
                    listener.onComponentCreationFailed(e);
                });
    }
}
