package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

public class Grocery {

    private static String householdId;
    private static FirebaseFirestore db;
    public Grocery(){}
    public Grocery(String householdId){
        this.householdId = householdId;
    }
    public String getHouseholdId() { return householdId; };

//    public static void createGroceryItem(String itemName){
//        GroceryItem item = new GroceryItem(itemName, householdId);
//
//        db = MyApplication.getDbInstance();
//        db.collection("groceries")
//                .add(item);
//
//    }


}