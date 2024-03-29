package com.example.homies.ui.grocery_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.GroceryItem;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.GroceryListViewModel;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private final String TAG = GroceryListFragment.class.getSimpleName();
    EditText itemET;
    RecyclerView recyclerViewGrocery;
    ArrayList<String> groceryItemsArrayList = new ArrayList<>();
    GroceryAdapter adapter;
    View view;
    private HouseholdViewModel householdViewModel;
    private GroceryListViewModel groceryListViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grocery_list, container, false);
        Timber.tag(TAG).d("onCreateView()");

        itemET = view.findViewById(R.id.editTextGroceryItem);

        final Button addItemButton = view.findViewById(R.id.addButton);
        if (addItemButton != null) {
            addItemButton.setOnClickListener(this);
        }

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

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {
            //Observe the selected household LiveData
            householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
                if (household != null) {
                    Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
                    groceryListViewModel.getItemsFromGroceryList(household.getHouseholdId());
                } else {
                    Timber.tag(TAG).d("No household selected.");
                }
            });


            //Observe the grocery items LiveData
            groceryListViewModel.getSelectedItems().observe(getViewLifecycleOwner(), items -> {
                if (items != null && !items.isEmpty()) {
                    Timber.tag(TAG).d("Grocery items: %s", items.size());
                    groceryItemsArrayList.clear();
                    for (GroceryItem item : items) {
                        groceryItemsArrayList.add(item.getItemName());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Timber.tag(TAG).d("No grocery items");
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        Timber.tag(TAG).d("onClick()");
        if (MyApplication.hasNetworkConnection(requireContext())) {
            if (view.getId() == R.id.addButton) {
                Timber.tag(TAG).d("add");
                String itemName = String.valueOf(itemET.getText());
                groceryListViewModel.addGroceryItem(itemName);
                groceryItemsArrayList.add(itemName);
                adapter.notifyDataSetChanged();
                itemET.getText().clear();
            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        itemET.getText().clear();
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
                        if (MyApplication.hasNetworkConnection(requireContext())) {
                            String itemName = groceryItemsArrayList.get(getAdapterPosition());
                            groceryListViewModel.deleteGroceryItem(itemName);
                            groceryItemsArrayList.remove(getAdapterPosition());
                            recyclerViewGrocery.post(() -> adapter.notifyDataSetChanged());
                        } else {
                            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
