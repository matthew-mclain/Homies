package com.example.homies.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.homies.MainActivity;
import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.ui.household.HouseholdActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import timber.log.Timber;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG).d("onCreate()");
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        // Set a single onClick listener for both sign-in and sign-up buttons
        Button signInButton = findViewById(R.id.buttonSignIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.tag(TAG).d("onStart()");

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userIsInHousehold(new OnCheckHouseholdListener() {
                @Override
                public void onCheckHousehold(boolean isInHousehold) {
                    Intent intent;
                    if (isInHousehold) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    } else {
                        intent = new Intent(getApplicationContext(), HouseholdActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public interface OnCheckHouseholdListener {
        void onCheckHousehold(boolean isInHousehold);
    }

    private void userIsInHousehold(OnCheckHouseholdListener listener) {
        mAuth = FirebaseAuth.getInstance();
        db = MyApplication.getDbInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Perform a query to check if the user is in a household
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String householdId = documentSnapshot.getString("householdId");
                            boolean isInHousehold = householdId != null;
                            listener.onCheckHousehold(isInHousehold);
                        } else {
                            listener.onCheckHousehold(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors here
                        listener.onCheckHousehold(false);
                    });
        } else {
            listener.onCheckHousehold(false);
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

    // Method to switch to SignUpFragment
    public void showSignUpFragment() {
        hideButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignUpFragment(this))
                .addToBackStack(null)
                .commit();
    }

    // Method to switch to SignInFragment
    public void showSignInFragment() {
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

