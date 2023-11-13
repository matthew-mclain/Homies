package com.example.homies.model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.Location;
import com.example.homies.model.LocationManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LocationViewModel extends ViewModel {

    private MutableLiveData<LocationManager> selectedLocationManager = new MutableLiveData<>();
    private MutableLiveData<List<Location>> selectedLocations = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationExistsLiveData = new MutableLiveData<>();
    private static FirebaseFirestore db;
    public boolean locationEnabled;

    private static final String TAG = LocationViewModel.class.getSimpleName();
    public LiveData<LocationManager> getSelectedLocationManager() { return selectedLocationManager; }
    public LiveData<List<Location>> getSelectedLocations() { return selectedLocations; }

    /*TODO: enble/disable location for different household */
    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }
    public void setSelectedLocationManager(LocationManager locationManager) {
        selectedLocationManager.setValue(locationManager);
    }

    public void setSelectedLocations(List<Location> locations) {
        selectedLocations.setValue(locations);
    }

    public LiveData<Boolean> getLocationExists() {
        return locationExistsLiveData;
    }

    public void setLocationExists(boolean locationExists) {
        locationExistsLiveData.setValue(locationExists);
    }

    public void getLocationsFromLocationManager(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("location_manager")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            LocationManager locationManager = doc.toObject(LocationManager.class);
                            setSelectedLocationManager(locationManager);
                            String locationManagerId = doc.getId();
                            Timber.tag(TAG).d("Location manager found with ID: %s", locationManagerId);
                            fetchLocations(locationManagerId);
                        } else {
                            // Handle failures
                            Timber.tag(TAG).e(task.getException(), "Error fetching locations for householdId: %s", householdId);
                        }
                    }
                });
    }

    private void fetchLocations(String locationManagerId) {
        db = MyApplication.getDbInstance();
        db.collection("location_manager")
                .document(locationManagerId)
                .collection("locations")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            List<Location> locations = new ArrayList<>();
                            for (DocumentSnapshot d : list) {
                                Timber.tag(TAG).d("locations: %s", d.getData().get("userId"));
                                Location location = d.toObject(Location.class);
                                locations.add(location);
                            }
                            setSelectedLocations(locations);
                        } else {
                            Timber.tag(TAG).d("No data found in Database");
                            selectedLocations.setValue(null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.tag(TAG).d("Fail to load data");
                    }
                });
    }

    public void addLocation(String longitude, String latitude, String userId) {
        Timber.tag(TAG).d("Adding location...");
        LocationManager locationManager = selectedLocationManager.getValue();
        if (locationManager != null) {
            locationManager.addLocation(longitude, latitude, userId);
            Timber.tag(TAG).d("%s's location added.", userId);
        } else {
            Timber.tag(TAG).d("No location manager selected");
        }
    }

    public void updateLocation(String longitude, String latitude, String userId) {
        LocationManager locationManager = selectedLocationManager.getValue();
        if(locationManager != null) {
            locationManager.updateLocation(longitude, latitude, userId);
            Timber.tag(TAG).d("%s's location updated.", userId);
        } else {
            Timber.tag(TAG).d("No location manager selected");
        }
    }

    public void deleteLocation(String userId) {
        LocationManager locationManager = selectedLocationManager.getValue();
        if (locationManager != null) {
            locationManager.deleteLocation(userId);
            Timber.tag(TAG).d("%s's location deleted.", userId);
        } else {
            Timber.tag(TAG).d("No location manager selected");
        }
    }

    public void checkIfLocationExists(String userId, String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("location_manager")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot locationManagerDocument = task.getResult().getDocuments().get(0);
                        locationManagerDocument.getReference()
                                .collection("locations")
                                .whereEqualTo("userId", userId)
                                .get()
                                .addOnCompleteListener(locationTask -> {
                                    if (locationTask.isSuccessful()) {
                                        // Check if the query result is not empty
                                        boolean locationExists = !locationTask.getResult().isEmpty();
                                        setLocationExists(locationExists);
                                    } else {
                                        // Handle errors in the second query
                                        Timber.e(locationTask.getException(), "Failed to check if location exists in locations subcollection.");
                                        setLocationExists(false);
                                    }
                                });

                    } else {
                        // Handle errors in the first query
                        Timber.e(task.getException(), "Failed to check if location exists in location_manager collection.");
                        setLocationExists(false);
                    }
                });
    }

}
