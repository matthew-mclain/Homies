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

public class GroceryItem {
    private String itemName;
    private static FirebaseFirestore db;
    private static final String TAG = GroceryItem.class.getSimpleName();

    public GroceryItem(){}

    public GroceryItem(String groceryItemName) {
        this.itemName = groceryItemName;
    }

    public String getItemName() {
        return itemName;
    }

}
