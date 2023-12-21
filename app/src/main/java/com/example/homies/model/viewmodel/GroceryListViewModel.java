package com.example.homies.model.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.GroceryList;
import com.example.homies.model.GroceryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryListViewModel extends ViewModel {
    private MutableLiveData<GroceryList> selectedGroceryList = new MutableLiveData<>();
    private MutableLiveData<List<GroceryItem>> selectedItems = new MutableLiveData<>();
    private static FirebaseFirestore db;
    private static final String TAG  = GroceryListViewModel.class.getSimpleName();

    public LiveData<GroceryList> getSelectedGroceryList() {
        return selectedGroceryList;
    }
    public LiveData<List<GroceryItem>> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedGroceryList(GroceryList groceryList) {
        selectedGroceryList.setValue(groceryList);
    }

    public void setSelectedItems(List<GroceryItem> groceryItems) {
        selectedItems.setValue(groceryItems);
    }

    public void getItemsFromGroceryList(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("grocery_lists")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            GroceryList groceryList = doc.toObject(GroceryList.class);
                            setSelectedGroceryList(groceryList);
                            String groceryListId = doc.getId();
                            Timber.tag(TAG).d("Grocery list found with ID: %s", groceryListId);
                            fetchGroceryItems(groceryListId);
                        } else {
                            // Handle failures
                            Timber.tag(TAG).e(task.getException(), "Error fetching grocery list for householdId: %s", householdId);
                        }
                    }
                });
    }



    private void fetchGroceryItems(String groceryListId) {
            db.collection("grocery_lists")
                    .document(groceryListId)
                    .collection("grocery_items")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                List<GroceryItem> items = new ArrayList<>();
                                for (DocumentSnapshot d : list) {
                                    Timber.tag(TAG).d("groceryItem: " + d.getData().get("itemName"));
                                    GroceryItem item = d.toObject(GroceryItem.class);
                                    items.add(item);
                                }
                                setSelectedItems(items);
                            } else {
                                Timber.tag(TAG).d("No data found in Database");
                                selectedItems.setValue(null);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.tag(TAG).d("Fail to load data");
                        }
                    });
    }

    public void addGroceryItem(String itemName) {
        GroceryList groceryList = selectedGroceryList.getValue();
        if (groceryList != null) {
            groceryList.addGroceryItem(itemName);
            Timber.tag(TAG).d(itemName + " added.");
        } else {
            Timber.tag(TAG).d("No grocery list selected.");
        }
    }

    public void deleteGroceryItem(String itemName) {
        GroceryList groceryList = selectedGroceryList.getValue();
        if (groceryList != null) {
            groceryList.deleteGroceryItem(itemName);
            Timber.tag(TAG).d(itemName + " deleted.");
        } else {
            Timber.tag(TAG).d("No grocery list selected.");
        }
    }

    public void updateGroceryItem(String oldItem, String newItem) {
        GroceryList groceryList = selectedGroceryList.getValue();
        if (groceryList != null) {
            groceryList.updateGroceryItem(oldItem, newItem);
            Timber.tag(TAG).d(oldItem + " updated.");
        } else {
            Timber.tag(TAG).d("No grocery list selected.");
        }
    }
}
