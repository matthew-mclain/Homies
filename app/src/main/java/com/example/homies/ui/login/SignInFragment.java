package com.example.homies.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.homies.R;

public class SignInFragment extends Fragment implements View.OnClickListener{

    EditText usernameET;
    EditText passwordET;

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        usernameET = view.findViewById((R.id.editTextSignInUsername));
        passwordET = view.findViewById(R.id.editTextSignInPassword);

        //adding on-click listener to sign in submit button
        final Button signInButton = view.findViewById(R.id.buttonSignInSubmit);
        if (signInButton != null){
            signInButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View view){
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        FragmentManager manager = getParentFragmentManager();

        //need to check from database for 1. if id exists 2. if password matches with id info
        if (username.equals("a") && password.equals("1")){
            //login success
        } else if (username.equals("") || password.equals("")) {
            //if empty field, error dialog (type 0)
            SignInErrorDialogFragment dialog = new SignInErrorDialogFragment(0);
            dialog.show(manager, "SignInError");
        } else {
            //if login fail, error dialog (type 1)
            SignInErrorDialogFragment dialog = new SignInErrorDialogFragment(1);
            dialog.show(manager, "SignInError");
        }
    }
}

