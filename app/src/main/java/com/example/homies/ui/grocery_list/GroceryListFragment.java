package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.GroceryList;
import com.example.homies.model.GroceryItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    EditText itemET, itemDeleteET, itemOldET, itemNewET;
    ListView groceryListLV;
    ArrayList<String> groceryArrayList;
    private static FirebaseFirestore db;
    View view;
    String householdId = "DS12fLdiL8w8uijmj9BJ";    //change this to get householdId from view model later

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);
        itemDeleteET = view.findViewById(R.id.deleteTextGroceryItem);
        itemOldET = view.findViewById(R.id.TextOldGroceryItem);
        itemNewET = view.findViewById(R.id.TextNewGroceryItem);

        groceryListLV = view.findViewById(R.id.groceryLV);
        groceryArrayList = new ArrayList<String>();
        db = MyApplication.getDbInstance();

        initializeListView();

        final Button addItemButton = view.findViewById(R.id.addButton);
        if (addItemButton != null) {
            addItemButton.setOnClickListener(this);
        }
        final Button deleteItemButton = view.findViewById(R.id.deleteButton);
        if (deleteItemButton != null) {
            deleteItemButton.setOnClickListener(this);
        }
        final Button updateItemButton = view.findViewById(R.id.updateButton);
        if (updateItemButton != null) {
            updateItemButton.setOnClickListener(this);
        }

        return view;
    }

    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.addButton) {
            Timber.tag(TAG).d("add");
            String itemName = String.valueOf(itemET.getText());
            GroceryList g = new GroceryList(householdId);
            GroceryItem.createGroceryItem(householdId, itemName);
        }
        if (view.getId() == R.id.deleteButton) {
            Timber.tag(TAG).d("delete");
            String itemName = String.valueOf(itemDeleteET.getText());
            GroceryItem.deleteGroceryItem(householdId, itemName);
        }

        if (view.getId() == R.id.updateButton) {
            Timber.tag(TAG).d("update");
            String oldItem = String.valueOf(itemOldET.getText());
            String newItem = String.valueOf(itemNewET.getText());
            GroceryItem.updateGroceryItem(householdId, oldItem, newItem);
        }

    }

    private void initializeListView() {
        groceryListLV = view.findViewById(R.id.groceryLV);

        db = FirebaseFirestore.getInstance();

        loadDatainListview();
    }

    private void loadDatainListview() {
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
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                            for (DocumentSnapshot d : list) {
                                                Timber.tag(TAG).d("groceryItem: " + d.getData().get("itemName"));
                                                groceryArrayList.add(d.getData().get("itemName").toString());
                                            }
                                            Timber.tag(TAG).d("Array?: " + groceryArrayList.toString());
                                            ArrayAdapter adapter = new ArrayAdapter<String> (getContext(), R.layout.grocery_lv_item, groceryArrayList);
                                            ListView listView = (ListView) view.findViewById(R.id.groceryLV);
                                            listView.setAdapter(adapter);
                                        } else {
                                            Timber.tag(TAG).d("No data found in Database");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.tag(TAG).d("Fail to load data");
                                    }
                                });
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        itemET.getText().clear();
        itemDeleteET.getText().clear();
        itemOldET.getText().clear();
        itemNewET.getText().clear();
    }
}
