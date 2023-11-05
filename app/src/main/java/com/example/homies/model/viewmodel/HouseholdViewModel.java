package com.example.homies.model.viewmodel;

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
    private static final String TAG = Household.class.getSimpleName();

    public LiveData<List<Household>> getUserHouseholds(String userId) {
        MutableLiveData<List<Household>> userHouseholdsLiveData = new MutableLiveData<>();

        db = MyApplication.getDbInstance();
        db.collection("households")
                .whereArrayContains("householdUsers", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Timber.tag(TAG).e("Error fetching user households");
                        return;
                    }

                    List<Household> userHouseholds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        Household household = document.toObject(Household.class);
                        userHouseholds.add(household);
                    }

                    userHouseholdsLiveData.setValue(userHouseholds);
                });

        return userHouseholdsLiveData;
    }

    public LiveData<Household> getSelectedHousehold() {
        return selectedHousehold;
    }

    public void setUserHouseholds(List<Household> households) {
        Timber.tag(TAG).d("Updating user households: %s", households);
        userHouseholds.setValue(households);
    }

    public void setSelectedHousehold(Household household) {
        selectedHousehold.setValue(household);
    }

    public void getHouseholdByName(String householdName) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .whereEqualTo("householdName", householdName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Household household = document.toObject(Household.class);
                            Timber.tag(TAG).d("Household found: %s", household.getHouseholdName());
                            selectedHousehold.setValue(household);
                            return; // Found the household, update LiveData and exit
                        }
                        // Household not found with the given name
                        selectedHousehold.setValue(null);
                    } else {
                        // Handle failures
                        selectedHousehold.setValue(null);
                    }
                });
    }
}
