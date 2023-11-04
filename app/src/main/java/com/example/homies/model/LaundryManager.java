package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
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

    public static void createMachine(String machineID, String machineName){
        Map<String, Object> data = new HashMap<>();
        data.put("machineName", machineName);
        data.put("startTime", null);
        data.put("duration", 60);
        data.put("usedBy", null);

        db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .document(machineID)
                .collection("machines")
                .document("123")
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("Machine Added: %s", machineID);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e("Error adding machine: %s", machineID);
                });
    }


    public static void deleteMachine(String machineID){
        // TO DO: check if dryer is being used
        db = MyApplication.getDbInstance();
        db.collection("laundry_managers")
                .document(machineID)
                .collection("machines")
                .document("123")
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("Machine deleted: %s", machineID);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e("Error deleting machine: %s", machineID);
                });
    }
}
