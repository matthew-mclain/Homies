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

    public static void createGroceryItem(String groceryListId, String groceryItemName) {
        GroceryItem groceryItem = new GroceryItem(groceryItemName, groceryListId);
        Timber.tag(TAG).d(groceryItem.toString());
        db = MyApplication.getDbInstance();
        db.collection("groceryLists")
                .document(groceryListId)
                .collection("groceryItems")
                .add(groceryItem)  // Add the GroceryItem to the specified GroceryList
                .addOnSuccessListener(documentReference -> {
                    String itemId = documentReference.getId();
                    Timber.tag(TAG).d("GroceryItem created with ID: %s", itemId);
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("itemId", itemId);
                    docData.put("itemName", groceryItemName);
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

    public static void deleteGroceryItem(String groceryListId, String itemName) {
        Timber.tag(TAG).d("groceryListId: "+ groceryListId);
        db = MyApplication.getDbInstance();
        db.collection("groceryLists").document(groceryListId)
                .collection("groceryItems")
                .whereEqualTo("itemName", itemName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.tag(TAG).d(document.getId() + " => " + document.getData());
                                //Delete
                                db.collection("groceryLists").document(groceryListId)
                                        .collection("groceryItems")
                                        .document(document.getId())
                                        .delete();
                            }
                            Timber.tag(TAG).d("delete success");
                        } else {
                            Timber.tag(TAG).d("?");
                        }
                    }
                });
    }

    public static void updateGroceryItem(String oldName, String newName, String groceryListId) {
        db = MyApplication.getDbInstance();
        db.collection("groceryLists").document(groceryListId)
                .collection("groceryItems")
                .whereEqualTo("itemName", oldName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.tag(TAG).d(document.getId() + " => " + document.getData());
                                //Update
                                db.collection("groceryLists").document(groceryListId)
                                        .collection("groceryItems")
                                        .document(document.getId())
                                        .update("itemName", newName);
                            }
                            Timber.tag(TAG).d("update success");
                        } else {
                            Timber.tag(TAG).d("?");
                        }
                    }
                });
    }

    public void setGroceryItemId(String id) {
        this.groceryItemId = id;
    }
}
