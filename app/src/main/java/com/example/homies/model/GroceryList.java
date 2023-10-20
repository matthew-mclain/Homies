package com.example.homies.model;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryList {
    private String groceryListId;
    private String householdId;
    private List<GroceryItem> groceryItems;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryList.class.getSimpleName();

    public GroceryList(String householdId){
        this.groceryListId = groceryListId;
        this.householdId = householdId;
        this.groceryItems = new ArrayList<>();
    }
    public String getHouseholdId() {
        return householdId;
    }

    public List<GroceryItem> getGroceryItems() {
        return groceryItems;
    }

    public void createGroceryList(String householdId) {
        GroceryList groceryList = new GroceryList(householdId);
        db.collection("groceryLists")
                .add(groceryList)
                .addOnSuccessListener(documentReference -> {
                    String groceryListId = documentReference.getId();
                    Timber.tag(TAG).d("GroceryList created with ID: %s", groceryListId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e,"Error creating GroceryList");
                });
    }
    public void deleteGroceryList(String groceryListId) {
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