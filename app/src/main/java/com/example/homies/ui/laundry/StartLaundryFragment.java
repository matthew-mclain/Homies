package com.example.homies.ui.laundry;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.Machine;
import com.example.homies.model.viewmodel.LaundryViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StartLaundryFragment extends Fragment implements View.OnClickListener {

    View view;

    private final String TAG = getClass().getSimpleName();
    private LaundryViewModel laundryViewModel;
    SeekBar seekBar;
    Spinner spinner;

    public StartLaundryFragment(LaundryViewModel laundryViewModel) {
        this.laundryViewModel = laundryViewModel;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_start_laundry, container, false);
        Timber.tag(TAG).d("onCreateView()");

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {
            //add machine list to the spinner
            spinner = view.findViewById(R.id.spinnerLaundryMachine);
            laundryViewModel.getLaundryMachines().observe(getViewLifecycleOwner(), machines -> {
                if (machines != null && !machines.isEmpty()) {
                    Timber.tag(TAG).d("Laundry Machines: %s", machines.size());
                    ArrayList<String> tempList = new ArrayList<>();
                    for (Machine machine : machines) {
                        tempList.add(machine.getName());
                    }

                    String[] listOfMachines = tempList.toArray(new String[tempList.size()]);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listOfMachines);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Timber.tag(TAG).d("No Laundry Machines");
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }


        //add seekbar listener so that it can show duration value
        seekBar = view.findViewById(R.id.seekBarDuration);
        final TextView seekBarValue = view.findViewById(R.id.textViewSeekbarValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        final Button startMachineButton = view.findViewById(R.id.startMachineButton);
        if (startMachineButton != null){
            startMachineButton.setOnClickListener(this);
        }

        final Button cancelRunButton = view.findViewById(R.id.cancelLaundryRun);
        if (cancelRunButton != null){
            cancelRunButton.setOnClickListener(this);
        }

        return view;
    }
    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");
        if (MyApplication.hasNetworkConnection(requireContext())) {
            if (v.getId() == R.id.startMachineButton) {
                Timber.tag(TAG).d("starting laundry on %s", spinner.getSelectedItem().toString());
                //update machine information
                laundryViewModel.updateMachineStatus(spinner.getSelectedItem().toString(), seekBar.getProgress());

                //change fragment
                getActivity().getSupportFragmentManager().popBackStack();
            } else if (v.getId() == R.id.cancelLaundryRun) {
                Timber.tag(TAG).d("cancel laundry running");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
