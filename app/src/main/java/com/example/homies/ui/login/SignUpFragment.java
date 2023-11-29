package com.example.homies.ui.login;

import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.User;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class SignUpFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener{

    EditText emailET;
    EditText passwordET;
    EditText passwordConfirmET;
    LoginActivity loginActivity;
    View view;
    FirebaseAuth mAuth;
    private final String TAG = getClass().getSimpleName();


    public SignUpFragment(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }
    public SignUpFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        Timber.tag(TAG).d("onCreateView()");


        mAuth = FirebaseAuth.getInstance();

        emailET = view.findViewById((R.id.editTextSignInEmail));
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
            String email = String.valueOf(emailET.getText());
            String password = String.valueOf(passwordET.getText());
            String passwordConfirm = String.valueOf(passwordConfirmET.getText());
            FragmentManager manager = getParentFragmentManager();

            //check if any field is empty
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)){
                // if yes for any, error dialog (type 0)
                SignUpErrorDialogFragment dialog = new SignUpErrorDialogFragment(0);
                dialog.show(manager, "SignInError");
                return;
            }

            if (MyApplication.hasNetworkConnection(requireContext())) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //Create User
                                String userId = mAuth.getCurrentUser().getUid();
                                String email1 = mAuth.getCurrentUser().getEmail();

                                User.createUser(userId, email1);

                                // Sign in success, update UI with the signed-in user's information
                                DialogInterface.OnClickListener dialogClickListener = (dialog, button) -> {
                                    if (button == DialogInterface.BUTTON_POSITIVE) {
                                        // Handle positive button click (YES)
                                        loginActivity.showSignInFragment();
                                    } else if (button == DialogInterface.BUTTON_NEGATIVE) {
                                        // Handle negative button click (NO)
                                        loginActivity.showButtons(view);
                                    }
                                };
                                new AlertDialog.Builder(requireActivity())
                                        .setTitle("Congratulations!")
                                        .setMessage("Successfully signed up for Homies!\nMove to Sign In page?")
                                        .setNegativeButton("NO", dialogClickListener)
                                        .setPositiveButton("YES", dialogClickListener)
                                        .create().show();
                            } else {
                                // If sign in fails, display a message to the user.
                                SignUpErrorDialogFragment dialog = new SignUpErrorDialogFragment(1);
                                dialog.show(manager, "SignInError");
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
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

