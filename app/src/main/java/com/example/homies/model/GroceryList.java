package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import timber.log.Timber;

public class GroceryList {
    private String householdId;
    private String groceryListId;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();

    public GroceryList(String householdId) {
        this.householdId = householdId;
    }

    public String getGroceryListId() {
        return groceryListId;
    }

    public void setGroceryListId(String groceryListId) {
        this.groceryListId = groceryListId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public static void createGroceryList(String householdId) {
        GroceryList groceryList = new GroceryList(householdId);

        db = MyApplication.getDbInstance();
        db.collection("grocery_lists")
                .add(groceryList)
                .addOnSuccessListener(documentReference -> {
                    // Grocery list created successfully, obtain its ID
                    String groceryListId = documentReference.getId();
                    groceryList.setGroceryListId(groceryListId);
                    Timber.tag(TAG).d("Grocery list created successfully: %s", groceryListId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating grocery list");
                });
    }
}
