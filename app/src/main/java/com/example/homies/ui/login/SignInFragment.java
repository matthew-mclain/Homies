package com.example.homies.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.homies.MainActivity;
import com.example.homies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;


public class SignInFragment extends Fragment implements View.OnClickListener{

    EditText emailET;
    EditText passwordET;
    LoginActivity loginActivity;
    View view;
    FirebaseAuth mAuth;
    private final String TAG = getClass().getSimpleName();

    public SignInFragment(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        Timber.tag(TAG).d("onCreateView()");

        mAuth = FirebaseAuth.getInstance();
        emailET = view.findViewById((R.id.editTextSignInEmail));
        passwordET = view.findViewById(R.id.editTextSignInPassword);

        //adding on-click listener to sign in submit button
        final Button signInButton = view.findViewById(R.id.buttonSignInSubmit);
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

        if (view.getId() == R.id.buttonSignInSubmit){
            String email = String.valueOf(emailET.getText());
            String password = String.valueOf(passwordET.getText());
            FragmentManager manager = getParentFragmentManager();

            //check if any field is empty
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                //if empty field, error dialog (type 0)
                SignInErrorDialogFragment dialog = new SignInErrorDialogFragment(0);
                dialog.show(manager, "SignInError");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                SignInErrorDialogFragment dialog = new SignInErrorDialogFragment(1);
                                dialog.show(manager, "SignInError");
                            }
                        }
                    });

        } else if (view.getId() == R.id.buttonBack) {
            loginActivity.showButtons(this.view);
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

