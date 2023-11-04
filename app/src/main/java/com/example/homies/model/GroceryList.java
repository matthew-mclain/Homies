package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;

public class GroceryList {
    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryList.class.getSimpleName();

    public GroceryList() {
    }

    public GroceryList(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdId() {
        return this.householdId;
    }

    //    public List<GroceryItem> getGroceryItems() {
    //        return groceryItems;
    //    }

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

    public void deleteGroceryList(String groceryListId) {
        db = MyApplication.getDbInstance();
        db.collection("grocery_lists")
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
}
