package com.example.homies.ui.household;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homies.R;
import com.example.homies.ui.login.SignInFragment;
import com.example.homies.ui.login.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class HouseholdActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private final String TAG = getClass().getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG).d("onCreate()");
        setContentView(R.layout.activity_household);
        mAuth = FirebaseAuth.getInstance();

        // Set a single onClick listener for both create and join buttons
        Button signInButton = findViewById(R.id.buttonCreateHousehold);
        Button signUpButton = findViewById(R.id.buttonJoinHousehold);

        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        // Set up the ActionBar to display a back arrow
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Enable back arrow
        }
    }

    @Override
    public void onClick(View v) {
        Button createHouseholdButton = findViewById(R.id.buttonCreateHousehold);
        Button buttonJoinHousehold = findViewById(R.id.buttonJoinHousehold);

        createHouseholdButton.setVisibility(View.GONE);
        buttonJoinHousehold.setVisibility(View.GONE);

        if (v.getId() == R.id.buttonCreateHousehold) {
            showCreateHouseholdFragment();
        } else if (v.getId() == R.id.buttonJoinHousehold) {
            showJoinHouseholdFragment();
        }
    }

    // Method to switch to CreateHouseholdFragment
    public void showCreateHouseholdFragment() {
        hideButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateHouseholdFragment(this))
                .addToBackStack(null)
                .commit();
    }

    // Method to switch to JoinHouseholdFragment
    public void showJoinHouseholdFragment() {
        hideButtons();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new JoinHouseholdFragment(this))
                .addToBackStack(null)
                .commit();
    }

    // Method to hide the buttons
    private void hideButtons() {
        Button createHouseholdButton = findViewById(R.id.buttonCreateHousehold);
        Button joinHouseholdButton = findViewById(R.id.buttonJoinHousehold);
        createHouseholdButton.setVisibility(View.GONE);
        joinHouseholdButton.setVisibility(View.GONE);
    }

    // Method to show the buttons
    public void showButtons(View viewToRemove) {
        LinearLayout fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.removeView(viewToRemove);
        Button createHouseholdButton = findViewById(R.id.buttonCreateHousehold);
        Button joinHouseholdButton = findViewById(R.id.buttonJoinHousehold);
        createHouseholdButton.setVisibility(View.VISIBLE);
        joinHouseholdButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity
            return true;
        }
        return super.onOptionsItemSelected(item);
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
