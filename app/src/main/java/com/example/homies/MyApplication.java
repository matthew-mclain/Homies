package com.example.homies;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class MyApplication extends Application {
    private static FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Timber for logging
        Timber.plant(new Timber.DebugTree());
    }

    public static FirebaseFirestore getDbInstance() {
        return db;
    }
}
