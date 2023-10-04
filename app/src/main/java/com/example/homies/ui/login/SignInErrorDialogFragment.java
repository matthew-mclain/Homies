package com.example.homies.ui.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class SignInErrorDialogFragment extends DialogFragment {


    int type;

    public SignInErrorDialogFragment(int type) {
        this.type = type;
    }
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog result;
        switch (type){
            case 0:
                result = new AlertDialog.Builder(requireActivity())
                        .setTitle("Sign In Error")
                        .setMessage("Empty Field")
                        .setPositiveButton("OK",
                                ((dialog, which) -> {
                                })).create();
                break;
            case 1:
                result = new AlertDialog.Builder(requireActivity())
                        .setTitle("Sign In Error")
                        .setMessage("Username Not Registered or Invalid Password")
                        .setPositiveButton("OK",
                                ((dialog, which) -> {
                                })).create();
                break;
            default:
                result = new AlertDialog.Builder(requireActivity())
                        .setTitle("Sign In Error")
                        .setMessage("Undefined Error")
                        .setPositiveButton("OK",
                                ((dialog, which) -> {
                                })).create();
                break;
        }
        return result;
    }
}
