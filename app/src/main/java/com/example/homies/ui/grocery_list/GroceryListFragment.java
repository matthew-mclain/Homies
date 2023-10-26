package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    EditText itemET, itemDeleteET, itemOldET, itemNewET;
    private ListView groceryList;
    ArrayList<String> groceryArrayList;
    DocumentReference reference;
    private static FirebaseFirestore db;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);
        itemDeleteET = view.findViewById(R.id.deleteTextGroceryItem);
        itemOldET = view.findViewById(R.id.TextOldGroceryItem);
        itemNewET = view.findViewById(R.id.TextNewGroceryItem);

        groceryList = view.findViewById(R.id.groceryLV);
        groceryArrayList = new ArrayList<String>();
        db = MyApplication.getDbInstance();

        //Retrieve data
        db.collection("groceryLists/123/groceryItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Timber.tag(TAG).d("isSuccessful");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timber.tag(TAG).d(task.getResult().toString());
                                Timber.tag(TAG).d(document.getId() + " => " + document.getData());
                            }
                        }
                    }
                    });

//        initializeListView();

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
            //GroceryList g = new GroceryList("123");
            //GroceryItem.createGroceryItem(g.getGroceryListId(), itemName);
            //g.createGroceryList("123", "123");
        }
        if (view.getId() == R.id.deleteButton) {
            Timber.tag(TAG).d("delete");
            String itemName = String.valueOf(itemDeleteET.getText());
            //GroceryList g = new GroceryList("123");
//            GroceryItem item = new GroceryItem(itemName, g.getGroceryListId());
            //GroceryItem.deleteGroceryItem(g.getGroceryListId(), itemName);
        }

        if (view.getId() == R.id.updateButton) {
            Timber.tag(TAG).d("update");
            String oldItem = String.valueOf(itemOldET.getText());
            String newItem = String.valueOf(itemNewET.getText());
            //String groceryListId = "123";
            //GroceryList g = new GroceryList("123");
//            GroceryItem item = new GroceryItem(oldItem, g.getHouseholdId());
            //GroceryItem.updateGroceryItem(oldItem, newItem, g.getGroceryListId());
        }

//        private void initializeListView() {
//            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, coursesArrayList);
//            db = MyApplication.getDbInstance();
////            reference = db.getReference();
//
//        }
    }
}
