package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.R;
import com.example.homies.model.GroceryItem;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.GroceryListViewModel;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    EditText itemET, itemDeleteET, itemOldET, itemNewET;
    RecyclerView recyclerViewGrocery;
    ArrayList<String> groceryItemsArrayList = new ArrayList<>();
    private static FirebaseFirestore db;
    GroceryAdapter adapter;
    View view;
    private HouseholdViewModel householdViewModel;
    private GroceryListViewModel groceryListViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);
//        itemDeleteET = view.findViewById(R.id.deleteTextGroceryItem);
//        itemOldET = view.findViewById(R.id.TextOldGroceryItem);
//        itemNewET = view.findViewById(R.id.TextNewGroceryItem);

        final Button addItemButton = view.findViewById(R.id.addButton);
        if (addItemButton != null) {
            addItemButton.setOnClickListener(this);
        }


//        final Button deleteItemButton = view.findViewById(R.id.deleteButton);
//        if (deleteItemButton != null) {
//            deleteItemButton.setOnClickListener(this);
//        }
//        final Button updateItemButton = view.findViewById(R.id.updateButton);
//        if (updateItemButton != null) {
//            updateItemButton.setOnClickListener(this);
//        }

        recyclerViewGrocery = view.findViewById(R.id.recyclerViewGrocery);

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
        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        groceryListViewModel = new ViewModelProvider(this).get(GroceryListViewModel.class);

        //Observe the selected household LiveData
        householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
            if (household != null) {
                Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
                // Fetch messages for the selected household's group chat
                groceryListViewModel.getItemsFromGroceryList(household.getHouseholdId());
            } else {
                Timber.tag(TAG).d("No household selected.");
            }
        });


        //Observe the grocery items LiveData
        groceryListViewModel.getSelectedItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                Timber.tag(TAG).d("Grocery items: " + items.size());
                groceryItemsArrayList.clear();
                for (GroceryItem item: items) {
                    groceryItemsArrayList.add(item.getItemName());
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
            groceryListViewModel.addGroceryItem(itemName);
            groceryItemsArrayList.add(itemName);
            adapter.notifyDataSetChanged();
            itemET.getText().clear();
        }
//        if (view.getId() == R.id.deleteButton) {
//            Timber.tag(TAG).d("delete");
//            String itemName = String.valueOf(itemDeleteET.getText());
//            groceryListViewModel.deleteGroceryItem(itemName);
//            groceryItemsArrayList.remove(itemName);
//            adapter.notifyDataSetChanged();
//            itemDeleteET.getText().clear();
//        }

//        if (view.getId() == R.id.updateButton) {
//            Timber.tag(TAG).d("update");
//            String oldItem = String.valueOf(itemOldET.getText());
//            String newItem = String.valueOf(itemNewET.getText());
//            groceryListViewModel.updateGroceryItem(oldItem, newItem);
//            int index = groceryItemsArrayList.indexOf(oldItem);
//            groceryItemsArrayList.set(index, newItem);
//            adapter.notifyDataSetChanged();
//            itemOldET.getText().clear();
//            itemNewET.getText().clear();
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        itemET.getText().clear();
//        itemDeleteET.getText().clear();
//        itemOldET.getText().clear();
//        itemNewET.getText().clear();
    }

    private class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {
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
            holder.itemCheckBox.setText(item);
            holder.itemCheckBox.setChecked(false);
        }

        @Override
        public int getItemCount() {
            return groceryItems.size();
        }

        public class GroceryViewHolder extends RecyclerView.ViewHolder {
            CheckBox itemCheckBox;
            public GroceryViewHolder(@NonNull View itemView) {
                super(itemView);
                itemCheckBox = itemView.findViewById(R.id.itemCheckbox);
                itemCheckBox.setChecked(false);
                itemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        String itemName = groceryItemsArrayList.get(getAdapterPosition());
                        groceryListViewModel.deleteGroceryItem(itemName);
                        groceryItemsArrayList.remove(getAdapterPosition());
                        recyclerViewGrocery.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }
    }
}
