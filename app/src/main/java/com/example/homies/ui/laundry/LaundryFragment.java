package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.homies.R;
import com.example.homies.model.Laundry;

import timber.log.Timber;

public class LaundryFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_laundry, container, false);
        Timber.tag(TAG).d("onCreateView()");

        machineNameET = view.findViewById(R.id.editTextMachineName);

        final Button addMachineButton = view.findViewById(R.id.addMachineButton);
        if (addMachineButton != null){
            addMachineButton.setOnClickListener(this);
        }

        return view;
    }
    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if(v.getId() == R.id.addMachineButton){
            Timber.tag(TAG).d("add machine");
            String machineName = machineNameET.getText().toString();
            Laundry machine = new Laundry("123", machineName);
            machine.createMachine("123", machineName);
        }
    }
}
