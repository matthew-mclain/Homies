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
import com.example.homies.model.Machine;
import com.example.homies.model.viewmodel.LaundryViewModel;

import timber.log.Timber;

public class EditLaundryMachineFragment  extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;

    private final String TAG = getClass().getSimpleName();
    LaundryViewModel laundryViewModel;
    Machine machine;

    public EditLaundryMachineFragment(LaundryViewModel laundryViewModel, Machine machine) {
        this.laundryViewModel = laundryViewModel;
        this.machine = machine;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_edit_laundry_machine, container, false);
        Timber.tag(TAG).d("onCreateView()");

        machineNameET = view.findViewById(R.id.editTextMachineNameToEdit);
        machineNameET.setText(machine.getName());

        final Button applyButton = view.findViewById(R.id.applyMachineEditButton);
        if (applyButton != null){
            applyButton.setOnClickListener(this);
        }

        final Button deleteButton = view.findViewById(R.id.deleteMachineButton);
        if (deleteButton != null){
            deleteButton.setOnClickListener(this);
        }

        final Button cancelButton = view.findViewById(R.id.cancelMachineEditButton);
        if (cancelButton != null){
            cancelButton.setOnClickListener(this);
        }

        return view;
    }
    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if (MyApplication.hasNetworkConnection(requireContext())) {

            if (v.getId() == R.id.applyMachineEditButton) {
                Timber.tag(TAG).d("edit machine information");
                //update name in database
                if (machine.getUsedBy() == null) {
                    laundryViewModel.updateMachineName(machine.getName(), machineNameET.getText().toString());
                    //change fragment
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    LaundryMachineEditErrorDialogFragment dialog = new LaundryMachineEditErrorDialogFragment(1);
                    dialog.show(getActivity().getSupportFragmentManager(), "Laundry Function Error");
                }

            } else if (v.getId() == R.id.deleteMachineButton) {
                Timber.tag(TAG).d("delete machine: %s", machine.getName());
                if (machine.getUsedBy() == null) {
                    //delete data from database if machine is not being used right now
                    laundryViewModel.deleteMachine(machine.getName());
                    //change fragment
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    //else show an error dialog
                    LaundryMachineEditErrorDialogFragment dialog = new LaundryMachineEditErrorDialogFragment(0);
                    dialog.show(getActivity().getSupportFragmentManager(), "Laundry Function Error");
                }


            } else if (v.getId() == R.id.cancelMachineEditButton) {
                Timber.tag(TAG).d("cancel machine information edit");

                //change fragment
                getActivity().getSupportFragmentManager().popBackStack();

            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}

