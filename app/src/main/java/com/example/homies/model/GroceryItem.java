package com.example.homies.model;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class GroceryItem {
    private String groceryItemId;
    private String groceryItemName;
    private String groceryListId;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryItem.class.getSimpleName();

    public GroceryItem(){}

    public GroceryItem(String groceryItemName, String groceryListId) {
        this.groceryItemName = groceryItemName;
        this.groceryListId = groceryListId;
    }

    public String getGroceryItemId() {
        return groceryItemId;
    }

    public String getGroceryItemName() {
        return groceryItemName;
    }

    public static void createGroceryItem(String groceryItemName, String groceryListId) {
        GroceryItem groceryItem = new GroceryItem(groceryItemName, groceryListId);
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .collection("groceryItems")
                .add(groceryItem)  // Add the GroceryItem to the specified GroceryList
                .addOnSuccessListener(documentReference -> {
                    String itemId = documentReference.getId();
                    Timber.tag(TAG).d("GroceryItem created with ID: %s", itemId);
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("itemID", itemId);
                    db.collection("groceryLists")
                            .document(groceryListId)
                            .collection("groceryItems")
                            .document(itemId)
                            .set(docData);
                    groceryItem.setGroceryItemId(itemId);
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

    public void setGroceryItemId(String id) {
        this.groceryItemId = id;
    }
}
