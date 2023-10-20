package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.example.homies.model.Grocery;
import com.example.homies.model.GroceryItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener{
    private final String TAG = getClass().getSimpleName();
    EditText itemET;
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);

        final Button addItemButton = view.findViewById(R.id.addButton);
        if (addItemButton != null) {
            addItemButton.setOnClickListener(this);
        }

        return view;
    }
    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.addButton) {
            Timber.tag(TAG).d("in if");
            String itemName = String.valueOf(itemET.getText());
            Grocery g = new Grocery("123");
//            GroceryItem item = new GroceryItem(itemName, g.getHouseholdId());
//            item.createGroceryItem(itemName);
            Map<String, Object> item = new HashMap<>();
            item.put("name", itemName);
            item.put("householdId", g.getHouseholdId());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("grocery")
                    .add(item);
            Timber.tag(TAG).d("?");
        }

    }
}
