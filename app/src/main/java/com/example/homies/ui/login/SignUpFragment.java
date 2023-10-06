package com.example.homies.ui.login;

import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.example.homies.R;

import timber.log.Timber;

public class SignUpFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener{

    EditText usernameET;
    EditText passwordET;
    EditText passwordConfirmET;
    LoginActivity loginActivity;
    View view;
    private final String TAG = getClass().getSimpleName();


    public SignUpFragment(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        Timber.tag(TAG).d("onCreateView()");


        usernameET = view.findViewById((R.id.editTextSignInUsername));
        passwordET = view.findViewById(R.id.editTextSignInPassword);
        passwordConfirmET = view.findViewById(R.id.editTextSignInConfirmPassword);

        //adding on-click listener to sign up submit button
        final Button signInButton = view.findViewById(R.id.buttonSignUpSubmit);
        if (signInButton != null){
            signInButton.setOnClickListener(this);
        }
        final Button backButton = view.findViewById(R.id.buttonBack);
        if (backButton != null){
            backButton.setOnClickListener(this);
        }


        return view;
    }

    @Override
    public void onClick(View view){
        Timber.tag(TAG).d("onClick()");

        if (view.getId() == R.id.buttonSignUpSubmit){
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
            else {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Congratulations!")
                        .setMessage("Successfully signed up for Homies!\nMove to Sign In page?")
                        .setNegativeButton("NO", this)
                        .setPositiveButton("YES", this)
                        .create().show();
            }

        } else if (view.getId() == R.id.buttonBack){
            loginActivity.showButtons(this.view);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        loginActivity.showButtons(this.view);
        if (which == BUTTON_POSITIVE){
            loginActivity.showSignInFragment();
        }
    }

    @Override
    public void onPause() {
        Timber.tag(TAG).d("onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.tag(TAG).d("onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.tag(TAG).d("onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("onDestroy()");
    }
}

