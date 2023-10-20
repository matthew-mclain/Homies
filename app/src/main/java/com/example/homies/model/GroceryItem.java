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
    private final String TAG = getClass().getSimpleName();
    private String itemName;
    private static String householdId;
    private static FirebaseFirestore db;

    public GroceryItem() {
    }

    public GroceryItem(String itemName, String householdId) {
        this.itemName = itemName;
        this.householdId = householdId;
    }

    public void createGroceryItem(String itemName){
//        GroceryItem item = new GroceryItem(itemName, householdId);

        Map<String, Object> item = new HashMap<>();
        item.put("name", itemName);
        item.put("householdId", householdId);

        db = MyApplication.getDbInstance();
        db.collection("grocery").document(itemName)
                .set(item);
        Timber.tag(TAG).d("?");
    }

    public void deleteItem(String itemName){
        db = MyApplication.getDbInstance();
        db.collection("grocery")
                .document(itemName)
                .delete();
    }

    public void updateItem(String oldName, String newName){
        db = MyApplication.getDbInstance();

        //Query database to find item by householdId and item's name
        db.collection("grocery").whereEqualTo("householdId", "2")
                .whereEqualTo("name", oldName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.tag(TAG).d(document.getId() + " => " + document.getData());
                                //Update
                                db.collection("grocery")
                                        .document(document.getId())
                                        .update("name", newName);
                            }
                            Timber.tag(TAG).d("update success");
                        } else {
                            Timber.tag(TAG).d("?");
                        }
                    }
                });
        Timber.tag(TAG).d("update wasn't performed");
    }
}


