package com.example.homies.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.homies.R;

public class SignInFragment extends Fragment implements View.OnClickListener {

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        Button backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonBack) {
            // Navigate back to the previous fragment (or activity in this case)
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onDestroyView() {
        ((LoginActivity) requireActivity()).showButtons();
        super.onDestroyView();
    }
}

