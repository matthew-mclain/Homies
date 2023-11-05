package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.homies.R;

import timber.log.Timber;

public class StartLaundryFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_start_laundry, container, false);
        Timber.tag(TAG).d("onCreateView()");

        //add machine list to the spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerLaundryMachine);
        final String[] listOfMachines = {"test", "test again"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,listOfMachines);
        spinner.setAdapter(adapter);

        //add seekbar listener so that it can show duration value
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarDuration);
        final TextView seekBarValue = (TextView) view.findViewById(R.id.textViewSeekbarValue);
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

        return view;
    }
    @Override
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if(v.getId() == R.id.startMachineButton){
            Timber.tag(TAG).d("starting laundry");
            //add data

            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new LaundryFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
