package com.example.homies;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

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

    public static boolean hasNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();

            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }

        return false;
    }
}
