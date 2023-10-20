package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroceryItem {
    private String itemName;
    private static String householdId;
    private static FirebaseFirestore db;

    public GroceryItem() {
    }

    public GroceryItem(String itemName, String householdId) {
        this.itemName = itemName;
        this.householdId = householdId;
    }

    public static void createGroceryItem(String itemName){
        GroceryItem item = new GroceryItem(itemName, householdId);

        db = MyApplication.getDbInstance();
        db.collection("groceries")
                .add(item);

    }

    public static void deleteItem(String itemName){
        db = MyApplication.getDbInstance();
        db.collection("groceries")
                .document(itemName)
                .delete();
    }

    public static void changeItem(String itemName){
        db = MyApplication.getDbInstance();
        db.collection("groceries")
                .document(itemName)
                .update("itemName", itemName);
    }

}


