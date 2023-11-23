package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.Machine;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.LaundryViewModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import timber.log.Timber;

public class LaundryFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;
    ArrayList<Machine> listOfMachines;
    private HouseholdViewModel householdViewModel;
    private LaundryViewModel laundryViewModel;
    RecyclerView laundryRecyclerView;
    MachineAdapter adapter;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_laundry, container, false);
        Timber.tag(TAG).d("onCreateView()");

        //link button with fragments
        final Button addMachineButton = view.findViewById(R.id.openAddLaundryFragment);
        if (addMachineButton != null){
            addMachineButton.setOnClickListener(this);
        }

        final Button useMachineButton = view.findViewById(R.id.useLaundryButton);
        if (useMachineButton != null){
            useMachineButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");

        //Initialize ViewModel instances
        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        laundryViewModel = new ViewModelProvider(requireActivity()).get(LaundryViewModel.class);

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {
            //Observe household livedata
            householdViewModel.getSelectedHousehold(requireContext())
                    .observe(getViewLifecycleOwner(), household -> {
                        if (household != null) {
                            Timber.tag(TAG).d("Selected Household Observed: %s", household.getHouseholdId());
                            laundryViewModel.getLaundryMachines(household.getHouseholdId());
                        } else {
                            Timber.tag(TAG).d("No Household Selected.");
                        }
                    });

            //Observe laundry machines livedata
            laundryViewModel.getLaundryMachines().observe(getViewLifecycleOwner(), machines -> {
                if (machines != null && !machines.isEmpty()) {
                    Timber.tag(TAG).d("Laundry Machines: %s", machines.size());
                    listOfMachines.clear();
                    for (Machine machine : machines) {
                        listOfMachines.add(machine);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Timber.tag(TAG).d("No Laundry Machines");
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        //fill in the list
        listOfMachines = new ArrayList<>();
        laundryRecyclerView = view.findViewById(R.id.recyclerViewLaundryList);
        adapter = new MachineAdapter(listOfMachines, getActivity().getSupportFragmentManager(), laundryViewModel);
        laundryRecyclerView.setAdapter(adapter);
        laundryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

    }

    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if(v.getId() == R.id.openAddLaundryFragment){
            Timber.tag(TAG).d("add machine fragment open");

            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddLaundryMachineFragment(laundryViewModel))
                    .addToBackStack(null)
                    .commit();

        } else if (v.getId() == R.id.useLaundryButton){
            Timber.tag(TAG).d("use machine fragment open");
            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StartLaundryFragment(laundryViewModel))
                    .addToBackStack(null)
                    .commit();
        }
    }
}
