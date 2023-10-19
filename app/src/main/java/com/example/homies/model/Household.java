package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Household {

    private String householdId;
    private String householdName;
    private CollectionReference householdUsers;
    private GroupChat groupChat;
    private GroceryList groceryList;
    private LaundryManager laundryManager;
    private Calendar calendar;
    private static FirebaseFirestore db;

    public Household(String householdId, String householdName){
        this.householdId = householdId;
        this.householdName = householdName;
        this.householdUsers = MyApplication.getDbInstance()
                .collection("households")
                .document(householdId)
                .collection("users");

        this.groupChat = new GroupChat();
        this.groceryList = new GroceryList();
        this.laundryManager = new LaundryManager();
        this.calendar = new Calendar();
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

    public static void createHousehold(String householdId, String householdName, User creatorUser) {
        Household household = new Household(householdId, householdName);

        // Add creator user to the household
        household.addUser(creatorUser);

        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(household.getHouseholdId())
                .set(household);
    }

    public static void deleteHousehold(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("households")
                .document(householdId)
                .delete();
    }

    public void joinHousehold() {
        //TODO
    }

    // Method to add a new user to the household
    public void addUser(User user) {
        householdUsers.document(user.getUserId()).set(user);
    }

    // Method to remove a user from the household
    public void removeUser(String userId) {
        householdUsers.document(userId).delete();
    }
}
