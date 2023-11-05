package com.example.homies.ui.laundry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class LaundryMachineDeleteErrorDialogFragment extends DialogFragment {


    int type;

    public LaundryMachineDeleteErrorDialogFragment(int type) {
        this.type = type;
    }
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog result;
        switch (type){
            case 0:
                result = new AlertDialog.Builder(requireActivity())
                        .setTitle("Laundry Function Error")
                        .setMessage("Cannot delete a machine that is currently being used. " +
                                "Please try again after finishing using it.")
                        .setPositiveButton("OK",
                                ((dialog, which) -> {
                                })).create();
                break;
            default:
                result = new AlertDialog.Builder(requireActivity())
                        .setTitle("Laundry Function Error")
                        .setMessage("Undefined Error")
                        .setPositiveButton("OK",
                                ((dialog, which) -> {
                                })).create();
                break;
        }
        return result;
    }
}
