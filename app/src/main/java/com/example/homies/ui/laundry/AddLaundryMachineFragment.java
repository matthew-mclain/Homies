package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.LaundryList;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.model.viewmodel.LaundryViewModel;

import timber.log.Timber;

public class AddLaundryMachineFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;

    private final String TAG = getClass().getSimpleName();

    private LaundryViewModel laundryViewModel;

    public AddLaundryMachineFragment(LaundryViewModel laundryViewModel){
        this.laundryViewModel = laundryViewModel;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_add_laundry_manchine, container, false);
        Timber.tag(TAG).d("onCreateView()");

        machineNameET = view.findViewById(R.id.editTextMachineNameToAdd);

        final Button addMachineButton = view.findViewById(R.id.addMachineButton);
        if (addMachineButton != null){
            addMachineButton.setOnClickListener(this);
        }

        final Button cancelButton = view.findViewById(R.id.cancelAddMachineButton);
        if (cancelButton != null){
            cancelButton.setOnClickListener(this);
        }

        return view;
    }
    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if (MyApplication.hasNetworkConnection(requireContext())) {
            if(v.getId() == R.id.addMachineButton){
                Timber.tag(TAG).d("add machine");
                String machineName = machineNameET.getText().toString();
                laundryViewModel.addLaundryMachine(machineName);

                //change fragment
                getActivity().getSupportFragmentManager().popBackStack();

            } else if (v.getId() == R.id.cancelAddMachineButton) {
                //change fragment
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
