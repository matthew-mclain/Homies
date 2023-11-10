package com.example.homies.model;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import timber.log.Timber;

public class LocationManager {
    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = Location.class.getSimpleName();
    public LocationManager() {}

    public LocationManager(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdId() {
        return this.householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public static void createLocationManager(String householdId) {
        LocationManager locationManager = new LocationManager(householdId);

        db = MyApplication.getDbInstance();
        db.collection("location_manager")
                .add(locationManager)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("Location Manager created successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating location manager");
                });
    }

    public void addLocation(String longitude, String latitude, String userId) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();
            Location location = new Location(longitude, latitude, userId);
            Timber.tag(TAG).d(householdId);

            db.collection("location_manager")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("location_manager")
                                    .document(doc.getId())
                                    .collection("locations")
                                    .add(location);
                        }
                    });
        }
    }

    public void updateLocation(String longitude, String latitude, String userId) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();
            db.collection("location_manager")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String locationManagerId = document.getId();

                                // Update the location if the user ID matches
                                db.collection("location_manager")
                                        .document(locationManagerId)
                                        .collection("locations")
                                        .whereEqualTo("userId", userId)
                                        .get()
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot locationDocument : updateTask.getResult()) {
                                                    // Update the latitude and longitude fields
                                                    locationDocument.getReference().update("latitude", latitude);
                                                    locationDocument.getReference().update("longitude", longitude);
                                                    Timber.tag(TAG).d("Location updated successfully for user: %s", userId);
                                                }
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Timber.tag(TAG).e(e, "Failed to update location for user: %s", userId);
                    });
        }
    }

    public void deleteLocation(String userId) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();

            db.collection("location_manager")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("location_manager")
                                    .document(doc.getId())
                                    .collection("locations")
                                    .whereEqualTo("userId", userId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot item = task.getResult().getDocuments().get(0);
                                            db.collection("location_manager")
                                                    .document(doc.getId())
                                                    .collection("locations")
                                                    .document(item.getId())
                                                    .delete();
                                            Timber.tag(TAG).d("delete success");
                                        }
                                    });
                        }
                    });
        }
    }
}
