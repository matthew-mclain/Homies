package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class GroceryItem {
    private final String TAG = getClass().getSimpleName();
    private String itemName;
    private static String householdId;
    private static FirebaseFirestore db;

    public GroceryItem() {
    }

    public GroceryItem(String itemName, String householdId) {
        this.itemName = itemName;
        this.householdId = householdId;
    }

    public void createGroceryItem(String itemName){
//        GroceryItem item = new GroceryItem(itemName, householdId);

        Map<String, Object> item = new HashMap<>();
        item.put("name", itemName);
        item.put("householdId", householdId);

        db = MyApplication.getDbInstance();
        db.collection("grocery").document(itemName)
                .set(item);
        Timber.tag(TAG).d("?");
    }

    public static void deleteItem(String itemName){
        db = MyApplication.getDbInstance();
        db.collection("grocery")
                .document(itemName)
                .delete();
    }

    public static void updateItem(String oldName, String newName){
        db = MyApplication.getDbInstance();
        db.collection("grocery")
                .document(oldName)
                .update("name", newName);
    }

}


