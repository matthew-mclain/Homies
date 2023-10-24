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

    public void setGroceryListId(String groceryListId) {
        this.groceryListId = groceryListId;
    }

    public interface OnComponentCreatedListener {
        void onComponentCreated(String componentId);
        void onComponentCreationFailed(Exception e);
    }

    public static void createGroceryList(String householdId, OnComponentCreatedListener listener) {
        GroceryList groceryList = new GroceryList(householdId);

        db = MyApplication.getDbInstance();
        db.collection("grocery_lists")
                .add(groceryList)
                .addOnSuccessListener(documentReference -> {
                    // Grocery list created successfully, obtain its ID
                    String groceryListId = documentReference.getId();
                    groceryList.setGroceryListId(groceryListId);
                    Timber.tag(TAG).d("Grocery list created successfully: %s", groceryListId);

                    // Invoke the listener to notify that the group chat has been created,
                    // and pass the generated groceryListId.
                    listener.onComponentCreated(groceryListId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).d("Error creating grocery list");

                    // Invoke the listener to notify about the failure.
                    listener.onComponentCreationFailed(e);
                });
    }
}
