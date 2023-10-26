package com.example.homies;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homies.ui.household.HouseholdActivity;
import com.example.homies.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import timber.log.Timber;


public class SplashActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // User is logged in, check if they are in a household
            checkUserHouseholdStatus(user.getUid());
        } else {
            // User is not logged in, launch LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkUserHouseholdStatus(String userId) {
        db = MyApplication.getDbInstance();

        db.collection("households")
                .whereArrayContains("householdUsers", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // User is in a household, launch MainActivity
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        // User is not in any household, launch HouseholdActivity
                        startActivity(new Intent(SplashActivity.this, HouseholdActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error checking user household status");
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                });
    }
}
