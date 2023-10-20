package com.example.homies.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Household {

    private String householdId;
    private String householdName;
    private CollectionReference householdUsers;
    private GroupChat groupChat;
    private GroceryList groceryList;
    private LaundryManager laundryManager;
    private Calendar calendar;
    private static FirebaseFirestore db;
    private static final String TAG = Household.class.getSimpleName();
    private static final MutableLiveData<List<Household>> householdsLiveData = new MutableLiveData<>();

    public static LiveData<List<Household>> getHouseholdsLiveData() {
        return householdsLiveData;
    }

    public Household(String householdId, String householdName){
        this.householdId = householdId;
        this.householdName = householdName;
        this.householdUsers = MyApplication.getDbInstance()
                .collection("households")
                .document(householdId)
                .collection("users");

        //this.groupChat = new GroupChat();
        //this.groceryList = new GroceryList();
        //this.laundryManager = new LaundryManager();
        //this.calendar = new Calendar();
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

    public static void getHouseholds(String householdId) {
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

    public static void createHousehold(String householdId, String householdName, User creatorUser) {
        Household household = new Household(householdId, householdName);

        // Add creator user to the household
        household.addUser(creatorUser);

        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(household.getHouseholdId())
                .set(household)
                .addOnSuccessListener(aVoid -> {
                    // Household created successfully
                    Timber.tag(TAG).d("Household created successfully: %s", household.getHouseholdId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error creating household: %s", household.getHouseholdId());
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
    public void addUser(User user) {
        householdUsers.document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // User added to household successfully
                    Timber.tag(TAG).d("User added to household: %s", user.getUserId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error adding user to household: %s", user.getUserId());
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
