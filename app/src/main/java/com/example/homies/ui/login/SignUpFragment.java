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

public class SignUpFragment extends Fragment implements View.OnClickListener{

    EditText usernameET;
    EditText passwordET;
    EditText passwordConfirmET;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        usernameET = view.findViewById((R.id.editTextSignInUsername));
        passwordET = view.findViewById(R.id.editTextSignInPassword);
        passwordConfirmET = view.findViewById(R.id.editTextSignInConfirmPassword);

        //adding on-click listener to sign up submit button
        final Button signInButton = view.findViewById(R.id.buttonSignUpSubmit);
        if (signInButton != null){
            signInButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View view){

        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        FragmentManager manager = getParentFragmentManager();


        //check if any field is empty
        if (password.equals("") || passwordConfirm.equals("") || username.equals("")){
            // if yes for any, error dialog (type 0)
            SignUpErrorDialogFragment dialog = new SignUpErrorDialogFragment(0);
            dialog.show(manager, "SignInError");
        }
        //check if two passwords matches
        else if (!password.equals(passwordConfirm)) {
            //if not, error dialog (type 1)
            SignUpErrorDialogFragment dialog = new SignUpErrorDialogFragment(1);
            dialog.show(manager, "SignInError");
        }
        //check if username is already taken

        //sign up completed


    }
}

