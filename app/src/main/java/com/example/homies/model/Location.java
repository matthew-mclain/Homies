package com.example.homies.model;

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
