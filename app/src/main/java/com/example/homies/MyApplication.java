package com.example.homies;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyApplication extends Application {
    private static FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getDbInstance() {
        return db;
    }
}
