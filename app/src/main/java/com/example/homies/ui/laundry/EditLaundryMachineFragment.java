package com.example.homies.ui.laundry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.homies.R;

import timber.log.Timber;

public class EditLaundryMachineFragment  extends Fragment implements View.OnClickListener {

    View view;
    EditText machineNameET;

    private final String TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_edit_laundry_machine, container, false);
        Timber.tag(TAG).d("onCreateView()");

        machineNameET = view.findViewById(R.id.editTextMachineNameToEdit);

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

        if(v.getId() == R.id.applyMachineEditButton){
            Timber.tag(TAG).d("edit machine information");
            //update name in database

            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new LaundryFragment())
                    .addToBackStack(null)
                    .commit();

        } else if(v.getId() == R.id.deleteMachineButton){
            Timber.tag(TAG).d("delete machine \""+"initial machine name"+"\"");
            if (true){
                //delete data from database if machine is not being used right now


                //change fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, new LaundryFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                //else show an error dialog
                LaundryMachineDeleteErrorDialogFragment dialog = new LaundryMachineDeleteErrorDialogFragment(0);
                dialog.show(getActivity().getSupportFragmentManager(), "Laundry Function Error");
            }


        } else if(v.getId() == R.id.cancelMachineEditButton){
            Timber.tag(TAG).d("cancel machine information edit");

            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new LaundryFragment())
                    .addToBackStack(null)
                    .commit();

        }
    }
}

