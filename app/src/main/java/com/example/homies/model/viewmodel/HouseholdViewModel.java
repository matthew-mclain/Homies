package com.example.homies.model.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.Household;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class HouseholdViewModel extends ViewModel {
    private MutableLiveData<List<Household>> userHouseholds = new MutableLiveData<>();
    private MutableLiveData<Household> selectedHousehold = new MutableLiveData<>();
    private static FirebaseFirestore db;
    private static final String TAG = HouseholdViewModel.class.getSimpleName();
    private static final String PREFERENCES = "MyPreferences";
    private static final String SELECTED_HOUSEHOLD = "selectedHousehold";

    public LiveData<List<Household>> getUserHouseholds(String userId) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .whereArrayContains("householdUsers", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Timber.tag(TAG).e("Error fetching user households");
                        return;
                    }

                    List<Household> households = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        Household household = document.toObject(Household.class);
                        households.add(household);
                    }

                    userHouseholds.setValue(households);
                });

        return userHouseholds;
    }

    public LiveData<Household> getSelectedHousehold(Context context) {
        Timber.tag(TAG).d("Getting selected household: %s", selectedHousehold);

        // Retrieve the selected household ID from SharedPreferences
        String selectedHouseholdId = getSelectedHouseholdIdFromPrefs(context);

        // If the selected household ID is not null, fetch the corresponding household object and set it as the selected household LiveData
        if (selectedHouseholdId != null) {
            getHouseholdById(context, selectedHouseholdId);
        }

        return selectedHousehold;
    }

    public void setUserHouseholds(List<Household> households) {
        Timber.tag(TAG).d("Updating user households: %s", households);
        userHouseholds.setValue(households);
    }

    public void setSelectedHousehold(Context context, Household household) {
        Timber.tag(TAG).d("Setting selected household: %s", selectedHousehold);
        saveSelectedHouseholdIdToPrefs(context, household.getHouseholdId()); // Assuming getId() returns the unique identifier of the household
        selectedHousehold.setValue(household);
    }

    // Save the selected household ID to SharedPreferences
    private void saveSelectedHouseholdIdToPrefs(Context context, String householdId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(SELECTED_HOUSEHOLD, householdId);
        editor.apply();
    }

    // Get the selected household ID from SharedPreferences
    private String getSelectedHouseholdIdFromPrefs(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getString(SELECTED_HOUSEHOLD, null);
    }

    public void getHouseholdByName(Context context, String householdName) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .whereEqualTo("householdName", householdName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Household household = document.toObject(Household.class);
                        setSelectedHousehold(context, household);
                        return; // Found the household, update LiveData and exit
                    }
                    // Household not found with the given name
                    setSelectedHousehold(context, null);
                })
                .addOnFailureListener(e -> {
                    // Handle failures
                    Timber.tag(TAG).e(e, "Error fetching household by name: %s", householdName);
                    setSelectedHousehold(context, null);
                });
    }

    private void getHouseholdById(Context context, String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(householdId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Household household = documentSnapshot.toObject(Household.class);
                    if (household != null) {
                        Timber.tag(TAG).d("Successfully retrieved household: %s", household.getHouseholdName());
                        setSelectedHousehold(context, household);
                    } else {
                        // Handle the case where no household is found
                        Timber.tag(TAG).d("No household found with ID: %s", householdId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Timber.tag(TAG).e(e, "Error fetching household with ID: %s", householdId);
                });
    }
}
