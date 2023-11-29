package com.example.homies.ui.login;

import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homies.R;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private final String TAG = getClass().getSimpleName();
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG).d("onCreate()");
        FirebaseAuth.getInstance().signOut();

        //checking if auto rotation is on
        if (android.provider.Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
            Toast.makeText(getApplicationContext(), "Auto Rotation is On", Toast.LENGTH_SHORT).show();

        } else{
            Toast.makeText(getApplicationContext(), "Auto Rotation is Off", Toast.LENGTH_LONG).show();
        }

        if (savedInstanceState != null){
            state = savedInstanceState.getInt("LoginState", 0);
        }

        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        // Set a single onClick listener for both sign-in and sign-up buttons
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        if (state == 1){
            showSignInFragment();
        } else if (state == 2){
            showSignUpFragment();
        }
    }

    @Override
    public void onClick(View v) {
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        signInButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);

        if (v.getId() == R.id.buttonSignIn) {
            showSignInFragment();
        } else if (v.getId() == R.id.buttonSignUp) {
            showSignUpFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("LoginState", state);
    }

    // Method to switch to SignUpFragment
    public void showSignUpFragment() {
        state = 2;
        hideButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment(this))
                .addToBackStack(null)
                .commit();
    }

    // Method to switch to SignInFragment
    public void showSignInFragment() {
        state = 1;
        hideButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignInFragment(this))
                .addToBackStack(null)
                .commit();
    }

    // Method to hide the buttons
    private void hideButtons() {
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);
        signInButton.setVisibility(View.GONE);
        signUpButton.setVisibility(View.GONE);
    }

    // Method to show the buttons
    public void showButtons(View viewToRemove) {
        state = 0;
        LinearLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.removeView(viewToRemove);
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);
        signInButton.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.tag(TAG).d("onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Timber.tag(TAG).d("onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.tag(TAG).d("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.tag(TAG).d("onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.tag(TAG).d("onDestroy()");
    }
}

