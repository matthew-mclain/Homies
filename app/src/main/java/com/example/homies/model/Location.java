package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

public class Location {
    private String longitude;
    private String latitude;
    private String userId;
    private static FirebaseFirestore db;
    private static final String TAG = Location.class.getSimpleName();
    public Location() {}
    public Location(String longitude, String latitude, String userId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getUserId() {return userId; }

    public interface DisplayNameCallbackLocation {
        void onCallback(String displayName);
    }

    public void getDisplayName(DisplayNameCallbackLocation callback) {
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String displayName = documentSnapshot.getString("displayName");
                        if (displayName != null) {
                            callback.onCallback(displayName);
                        }
                    } else {
                        // Handle the case where the document does not exist
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure scenario
                    callback.onCallback(null);
                });
    }

//    public void setLongitude(String longitude) {
//        this.longitude = longitude;
//    }
//
//    public void setLatitude(String latitude) {
//        this.latitude = latitude;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
}
