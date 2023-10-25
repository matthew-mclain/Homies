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

    public GroceryList() {
    }

    public GroceryList(String householdId) {
        this.householdId = householdId;
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
                    Timber.tag(TAG).d("Grocery list created successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating grocery list");
                });
    }
}
