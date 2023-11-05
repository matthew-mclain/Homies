package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.R;
import com.example.homies.model.Laundry;

import java.util.ArrayList;

import timber.log.Timber;

public class LaundryFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;
    ArrayList<Machine> listOfMachines;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_laundry, container, false);
        Timber.tag(TAG).d("onCreateView()");

        //fill in the list
        RecyclerView listRecyclerView = (RecyclerView) view.findViewById(R.id.LaundryList);
        listOfMachines = new Machine(null, null, null).getLaundryMachinesList("ID");
        MachineAdapter adapter = new MachineAdapter(listOfMachines, getActivity().getSupportFragmentManager());
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
    public void onClick(View v) {
        Timber.tag(TAG).d("onClick()");

        if(v.getId() == R.id.openAddLaundryFragment){
            Timber.tag(TAG).d("add machine fragment open");
            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddLaundryMachineFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (v.getId() == R.id.useLaundryButton){
            Timber.tag(TAG).d("use machine fragment open");
            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new StartLaundryFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
