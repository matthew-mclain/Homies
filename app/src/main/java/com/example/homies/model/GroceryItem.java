package com.example.homies.model;

import androidx.annotation.NonNull;

import com.example.homies.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private static FirebaseFirestore db;
    private static final String TAG = GroceryItem.class.getSimpleName();

    public GroceryItem(){}

    public GroceryItem(String groceryItemId, String groceryItemName) {
        this.groceryItemId = groceryItemId;
        this.groceryItemName = groceryItemName;
    }

    public String getGroceryItemId() {
        return groceryItemId;
    }

    public String getGroceryItemName() {
        return groceryItemName;
    }

    public static void createGroceryItem(String householdId, String groceryItemName) {
        db = MyApplication.getDbInstance();

        GroceryItem groceryItem = new GroceryItem(groceryItemName, householdId);
        Timber.tag(TAG).d(groceryItem.toString());

        db.collection("grocery_lists")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            db.collection("grocery_lists")
                                    .document(doc.getId())
                                    .collection("grocery_items")
                                    .add(groceryItem)
                                    .addOnSuccessListener(documentReference -> {
                                        String itemId = documentReference.getId();
                                        Map<String, Object> docData = new HashMap<>();
                                        docData.put("itemId", itemId);
                                        docData.put("itemName", groceryItemName);
                                        db.collection("grocery_lists")
                                                .document(doc.getId())
                                                .collection("grocery_items")
                                                .document(itemId)
                                                .set(docData);
                                        groceryItem.setGroceryItemId(itemId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Timber.tag(TAG).e(e, "Error creating grocery item");
                                    });
                        }
                    }
                });
    }

    public static void deleteGroceryItem(String householdId, String itemName) {
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

    public static void updateGroceryItem(String householdId, String oldName, String newName) {
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

    public void setGroceryItemId(String id) {
        this.groceryItemId = id;
    }
}
