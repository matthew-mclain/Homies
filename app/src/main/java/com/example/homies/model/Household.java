package com.example.homies.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class Household {

    private String householdId;
    private String householdName;
    private CollectionReference householdUsers;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();
    private static final MutableLiveData<List<Household>> householdsLiveData = new MutableLiveData<>();

    public static LiveData<List<Household>> getHouseholdsLiveData() {
        return householdsLiveData;
    }

    public Household(String householdName){
        this.householdName = householdName;
    }

    // Setter for householdId
    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public void setHouseholdUsers(String householdId) {
        this.householdUsers = MyApplication.getDbInstance()
                .collection("households")
                .document(householdId)
                .collection("users");
    }

    public String getHouseholdId() {
        return householdId;
    }

    public String getHouseholdName() {
        return householdName;
    }

    // Method to get the collection of users associated with this household
    public CollectionReference getUsersCollection() {
        return householdUsers;
    }

    public static void getHousehold(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(householdId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Household data retrieved successfully
                        Timber.tag(TAG).d("Household data retrieved: %s", documentSnapshot.getData());
                    } else {
                        // Household document does not exist
                        Timber.tag(TAG).w("Household document does not exist for household ID: %s", householdId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error getting household data for household ID: %s", householdId);
                });
    }

    public static void getAllHouseholds() {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Household> householdsList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Household household = document.toObject(Household.class);
                        householdsList.add(household);
                    }
                    householdsLiveData.setValue(householdsList);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error getting households data");
                });
    }

    public static void createHousehold(String householdName, String userId) {
        Household household = new Household(householdName);

        db = MyApplication.getDbInstance();
        db.collection("households")
                .add(household)
                .addOnSuccessListener(documentReference -> {
                    // Household created successfully
                    String householdId = documentReference.getId();

                    // Add creator user to the household
                    household.addUser(householdId, userId);
                    household.setHouseholdUsers(householdId);

                    Timber.tag(TAG).d("Household created successfully: %s", householdId);

                    // Create GroupChat, GroceryList, LaundryManager, Calendar
                    GroupChat.createGroupChat(householdId);
                    GroceryList.createGroceryList(householdId);
                    LaundryManager.createLaundryManager(householdId);
                    Calendar.createCalendar(householdId);

                })
                .addOnFailureListener(e -> {
                        // Handle errors
                        Timber.tag(TAG).e(e, "Error creating household");
                    });

    }

    public static void deleteHousehold(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(householdId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Household deleted successfully
                    Timber.tag(TAG).d("Household deleted successfully: %s", householdId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error deleting household: %s", householdId);
                });
    }

    public void joinHousehold() {
        //TODO
    }

    // Method to add a new user to the household
    public void addUser(String householdId, String userId) {
        db = MyApplication.getDbInstance();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);

        db.collection("households")
                .document(householdId)
                .collection("users")
                .document(userId) // Use userId as the document ID in the subcollection
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // User added to the household successfully
                    Timber.tag(TAG).d("User added to household %s: %s", householdId, userId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error adding user to household %s: %s", householdId, userId);
                });
    }

    // Method to remove a user from the household
    public void removeUser(String userId) {
        householdUsers.document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    // User removed from household successfully
                    Timber.tag(TAG).d("User removed from household: %s", userId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error removing user from household: %s", userId);
                });
    }
}
