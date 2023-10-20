package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class GroceryItem {
    private String groceryItemId;
    private String groceryItemName;
    private String groceryListId;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryItem.class.getSimpleName();

    public GroceryItem(String groceryItemId, String groceryItemName, String groceryListId) {
        this.groceryItemId = groceryItemId;
        this.groceryItemName = groceryItemName;
        this.groceryListId = groceryListId;
    }

    public static void createGroceryItem(String groceryItemId, String groceryItemName, String groceryListId) {
        GroceryItem groceryItem = new GroceryItem(groceryItemId, groceryItemName, groceryListId);
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .collection("groceryItems")
                .add(groceryItem)  // Add the GroceryItem to the specified GroceryList
                .addOnSuccessListener(documentReference -> {
                    String itemId = documentReference.getId();
                    Timber.tag(TAG).d("GroceryItem created with ID: %s", itemId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e, "Error creating GroceryItem");
                });
    }

    public void deleteGroceryItem(String groceryListId, String groceryItemId) {
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .collection("groceryItems")
                .document(groceryItemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Timber.tag(TAG).d("GroceryItem with ID: %s deleted successfully", groceryItemId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e, "Error deleting GroceryItem");
                });
    }

    public void updateGroceryItem(String newName, String groceryItemId, String groceryListId) {
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .collection("groceryItems")
                .document(groceryItemId)
                .update("groceryItemName", newName)
                .addOnSuccessListener(aVoid -> {
                    Timber.tag(TAG).d("GroceryItem with ID: %s updated successfully", groceryItemId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Timber.tag(TAG).e(e, "Error updating GroceryItem");
                });
    }

    public String getGroceryItemId() {
        return groceryItemId;
    }
}


