package com.example.homies.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homies.MainActivity;
import com.example.homies.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if the user is already logged in, if so, navigate to MainActivity
        if (userIsLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Set a single onClick listener for both sign-in and sign-up buttons
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSignIn) {
            showSignInFragment();
        } else if (v.getId() == R.id.buttonSignUp) {
            showSignUpFragment();
        }
    }

    // Method to check if the user is already logged in
    private boolean userIsLoggedIn() {
        // Implement your logic to check if the user is logged in
        // For example, you can check shared preferences, a local database, or an API
        // Return true if logged in, false otherwise
        return false; // Return false for demonstration purposes
    }

    // Method to switch to SignUpFragment
    public void showSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment())
                .addToBackStack(null)
                .commit();
    }

    // Method to switch to SignInFragment
    public void showSignInFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignInFragment())
                .addToBackStack(null)
                .commit();
    }
}

