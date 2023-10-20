package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class GroceryList {
    private String groceryListId;
    private String householdId;
//    private List<GroceryItem> groceryItems;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryList.class.getSimpleName();

    public GroceryList(String householdId){
        this.groceryListId = groceryListId;
        this.householdId = householdId;
//        this.groceryItems = new ArrayList<>();
    }
    public String getHouseholdId() {
        return householdId;
    }

//    public List<GroceryItem> getGroceryItems() {
//        return groceryItems;
//    }

    public void createGroceryList(String householdId) {
        GroceryList groceryList = new GroceryList(householdId);
        Map<String, Object> docData = new HashMap<>();
        docData.put("householdId", householdId);
        db = MyApplication.getDbInstance();
        db.collection("groceryLists").document(householdId)
                .set(docData)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("GroceryList created with ID: %s", householdId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e,"Error creating GroceryList");
                });
    }
    public void deleteGroceryList(String groceryListId) {
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Timber.tag(TAG).d("GroceryList with ID: %s deleted successfully", groceryListId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e, "Error deleting GroceryList with ID: %s", groceryListId);
                });
    }

    public String getGroceryListId() {
        return groceryListId;
    }
}