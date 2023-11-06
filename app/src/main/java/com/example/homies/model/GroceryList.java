package com.example.homies.model;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

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

    public void addGroceryItem(String itemName) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();
            GroceryItem groceryItem = new GroceryItem(itemName);
            Timber.tag(TAG).d(householdId);

            db.collection("grocery_lists")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Timber.tag(TAG).d("here");
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                db.collection("grocery_lists")
                                        .document(doc.getId())
                                        .collection("grocery_items")
                                        .add(groceryItem);
                            }
                        }
                    });
        }
    }

    public void deleteGroceryItem(String itemName) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();

            db.collection("grocery_lists")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("grocery_lists")
                                    .document(doc.getId())
                                    .collection("grocery_items")
                                    .whereEqualTo("itemName", itemName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot item = task.getResult().getDocuments().get(0);
                                            db.collection("grocery_lists")
                                                    .document(doc.getId())
                                                    .collection("grocery_items")
                                                    .document(item.getId())
                                                    .delete();
                                            Timber.tag(TAG).d("delete success");
                                        }
                                    });
                        }
                    });
        }
    }

    public void updateGroceryItem(String oldName, String newName) {
        if (householdId != null) {
            db = MyApplication.getDbInstance();

            db.collection("grocery_lists")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("grocery_lists")
                                    .document(doc.getId())
                                    .collection("grocery_items")
                                    .whereEqualTo("itemName", oldName)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot item = task.getResult().getDocuments().get(0);
                                            db.collection("grocery_lists")
                                                    .document(doc.getId())
                                                    .collection("grocery_items")
                                                    .document(item.getId())
                                                    .update("itemName", newName);
                                            Timber.tag(TAG).d("update success");
                                        }
                                    });
                        }
                    });
        }
    }
}
