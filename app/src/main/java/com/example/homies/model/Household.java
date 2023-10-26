package com.example.homies.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class Household {

    private String householdId;
    private String householdName;
    private ArrayList<String> householdUsers;
    private static FirebaseFirestore db;
    private static final String TAG = Household.class.getSimpleName();
    private static final MutableLiveData<List<Household>> householdsLiveData = new MutableLiveData<>();

    public static LiveData<List<Household>> getHouseholdsLiveData() {
        return householdsLiveData;
    }

    public Household() {
    }

    public Household(String householdName){
        this.householdName = householdName;
        this.householdUsers = new ArrayList<>();
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public ArrayList<String> getHouseholdUsers() {
        return householdUsers;
    }

    public void setHouseholdUsers(ArrayList<String> householdUsers) {
        this.householdUsers = householdUsers;
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
                    household.setHouseholdId(householdId);

                    // Add user to the household
                    household.addUser(userId);
                    household.updateHouseholdInFirestore();

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

    public void addUser(String userId) {
        if (!householdUsers.contains(userId)) {
            householdUsers.add(userId);
            updateUsersInFirestore();
        }
    }

    // Method to remove a user from the household
    public void removeUser(String userId) {
        householdUsers.remove(userId);
        updateUsersInFirestore();
    }

    private void updateUsersInFirestore() {
        // Convert ArrayList to a Map where keys are user IDs and values are true
        Map<String, Boolean> userMap = new HashMap<>();
        for (String userId : householdUsers) {
            userMap.put(userId, true);
        }

        // Update the "users" subcollection in Firestore with the updated user list (as a Map)
        db.collection("households")
                .document(householdId)
                .collection("users")
                .document("userListDocument")
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // User list updated in Firestore successfully
                    Timber.tag(TAG).d("User list updated in Firestore for household: %s", householdId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error updating user list in Firestore for household: %s", householdId);
                });
    }

    private void updateHouseholdInFirestore() {
        db.collection("households")
                .document(householdId)
                .set(this)
                .addOnSuccessListener(aVoid -> {
                    // Household data updated in Firestore successfully
                    Timber.tag(TAG).d("Household data updated in Firestore for ID: %s", householdId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error updating household data in Firestore for ID: %s", householdId);
                });
    }
}
