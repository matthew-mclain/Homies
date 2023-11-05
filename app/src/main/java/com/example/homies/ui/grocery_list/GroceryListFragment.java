package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.GroceryList;
import com.example.homies.model.GroceryItem;
import com.example.homies.viewmodel.GroceryListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;


import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    EditText itemET, itemDeleteET, itemOldET, itemNewET;
//    ListView groceryListLV;
    RecyclerView recyclerViewGrocery;
    ArrayList<String> groceryItemsArrayList = new ArrayList<>();
    private static FirebaseFirestore db;
    GroceryAdapter adapter;
    View view;
//    private HouseholdViewModel householdViewModel;
    private GroceryListViewModel groceryListViewModel;
    String householdId = "DS12fLdiL8w8uijmj9BJ";    //change this to get householdId from view model later

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);
        itemDeleteET = view.findViewById(R.id.deleteTextGroceryItem);
        itemOldET = view.findViewById(R.id.TextOldGroceryItem);
        itemNewET = view.findViewById(R.id.TextNewGroceryItem);

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

        recyclerViewGrocery = view.findViewById(R.id.recyclerViewGrocery);

//        groceryListLV = view.findViewById(R.id.groceryLV);

//        db = MyApplication.getDbInstance();
//        adapter = new ArrayAdapter<String> (getContext(), R.layout.grocery_lv_item, R.id.checkbox, groceryArrayList);

//        initializeListView();

        adapter = new GroceryAdapter(groceryItemsArrayList);

        recyclerViewGrocery.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewGrocery.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");


        //Initialize ViewModel instances
//        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        groceryListViewModel = new ViewModelProvider(this).get(GroceryListViewModel.class);

        // Observe the selected household LiveData
//        householdViewModel.getSelectedHousehold().observe(getViewLifecycleOwner(), household -> {
//            if (household != null) {
//                Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
//                // Fetch messages for the selected household's group chat
//                groupChatViewModel.getMessagesForGroupChat(household.getHouseholdId());
//            } else {
//                Timber.tag(TAG).d("No household selected.");
//            }
//        });
        groceryListViewModel.getItemsFromGroceryList(householdId);  //replace with above later


        //Observe the grocery items LiveData
        groceryListViewModel.getSelectedItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                Timber.tag(TAG).d("Grocery items: " + items.size());
                groceryItemsArrayList.clear();
                for (GroceryItem item: items) {
                    groceryItemsArrayList.add(item.getGroceryItemName());
                }
                adapter.notifyDataSetChanged();
            } else {
                Timber.tag(TAG).d("No grocery items");
            }
        });
    }

    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.addButton) {
            Timber.tag(TAG).d("add");
            String itemName = String.valueOf(itemET.getText());
            GroceryItem.createGroceryItem(householdId, itemName);
//            groceryItemsArrayList.add(itemName);
//            adapter.notifyDataSetChanged();
        }
        if (view.getId() == R.id.deleteButton) {
            Timber.tag(TAG).d("delete");
            String itemName = String.valueOf(itemDeleteET.getText());
            GroceryItem.deleteGroceryItem(householdId, itemName);
//            groceryItemsArrayList.remove(itemName);
//            adapter.notifyDataSetChanged();
        }

        if (view.getId() == R.id.updateButton) {
            Timber.tag(TAG).d("update");
            String oldItem = String.valueOf(itemOldET.getText());
            String newItem = String.valueOf(itemNewET.getText());
            GroceryItem.updateGroceryItem(householdId, oldItem, newItem);
//            int index = groceryItemsArrayList.indexOf(oldItem);
//            groceryItemsArrayList.set(index, newItem);
//            adapter.notifyDataSetChanged();
        }

    }

//    private void initializeListView() {
//        groceryListLV = view.findViewById(R.id.groceryLV);
//
//        db = FirebaseFirestore.getInstance();
//
//        loadDatainListview();
//    }

//    private void loadDatainListview() {
//        db.collection("grocery_lists")
//                .whereEqualTo("householdId", householdId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
//                        db.collection("grocery_lists")
//                                .document(doc.getId())
//                                .collection("grocery_items")
//                                .get()
//                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        if (!queryDocumentSnapshots.isEmpty()) {
//                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                                            for (DocumentSnapshot d : list) {
//                                                Timber.tag(TAG).d("groceryItem: " + d.getData().get("itemName"));
//                                                groceryItemsArrayList.add(d.getData().get("itemName").toString());
//                                            }
//                                            Timber.tag(TAG).d("Array?: " + groceryItemsArrayList.toString());
//                                            ListView listView = (ListView) view.findViewById(R.id.groceryLV);
//                                            listView.setAdapter(adapter);
//                                        } else {
//                                            Timber.tag(TAG).d("No data found in Database");
//                                        }
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Timber.tag(TAG).d("Fail to load data");
//                                    }
//                                });
//                    }
//                });
//    }

    @Override
    public void onResume() {
        super.onResume();
        itemET.getText().clear();
        itemDeleteET.getText().clear();
        itemOldET.getText().clear();
        itemNewET.getText().clear();
    }

    private static class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
        private List<String> groceryItems;

        public GroceryAdapter(List<String> groceryItems) {
            this.groceryItems = groceryItems;
        }

        @NonNull
        @Override
        public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_lv_item, parent, false);
            return new GroceryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
            String item = groceryItems.get(position);
            holder.itemTextView.setText(item);
        }

        @Override
        public int getItemCount() {
            return groceryItems.size();
        }

        public static class GroceryViewHolder extends RecyclerView.ViewHolder {
            TextView itemTextView;
            public GroceryViewHolder(@NonNull View itemView) {
                super(itemView);
                itemTextView = itemView.findViewById(R.id.itemTextView);
            }
        }
    }
}
