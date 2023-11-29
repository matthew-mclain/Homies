package com.example.homies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.homies.model.Household;
import com.example.homies.model.User;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.ui.calendar.CalendarFragment;
import com.example.homies.ui.grocery_list.GroceryListFragment;
import com.example.homies.ui.household.HouseholdActivity;
import com.example.homies.ui.laundry.LaundryFragment;
import com.example.homies.ui.location.LocationFragment;
import com.example.homies.ui.messages.MessagesFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import java.util.Objects;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public BottomNavigationView bottomNavigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private HouseholdViewModel householdViewModel;
    private static FirebaseFirestore db;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREFERENCES = "MyPreferences";
    private static final String SELECTED_HOUSEHOLD = "selectedHousehold";
    private static final String PREF_THEME_KEY = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        updateNavDrawerHeader();

        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_messages);
        }

        // Observe the user's households from the ViewModel
        householdViewModel = new ViewModelProvider(this).get(HouseholdViewModel.class);
        householdViewModel.getUserHouseholds(getCurrentUserId()).observe(this, households -> {
            // Update the navigation drawer menu with household names
            Timber.tag(TAG).d("Received user households: %s", households);
            Menu navMenu = navigationView.getMenu();

            // Clear the items in the group_households group
            navMenu.removeGroup(R.id.group_households);

            // Get the selected household ID from SharedPreferences
            SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String selectedHouseholdId = preferences.getString(SELECTED_HOUSEHOLD, "");

            //Add households to Navdrawer
            for (int i = 0; i < households.size(); i++) {
                Household household = households.get(i);
                Timber.tag(TAG).d("Adding household: %s", household.getHouseholdName());
                MenuItem menuItem = navMenu.add(R.id.group_households, i, Menu.NONE, household.getHouseholdName()).setCheckable(true);
                menuItem.setIcon(R.drawable.household_24);

                // Check if the current household is the selected one and highlight it
                if (selectedHouseholdId.equals(household.getHouseholdId())) {
                    menuItem.setChecked(true);
                }
            }
        });

        //Create new MessagesFragment
        replaceFragment(new MessagesFragment());

        //Handle Navbar and Navdrawer Clicks
        handleNavbarClicks();
        handleNavdrawerClicks(householdViewModel);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void handleNavbarClicks() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_messages) {
                replaceFragment(new MessagesFragment());
            } else if (id == R.id.navigation_grocery_list) {
                replaceFragment(new GroceryListFragment());
            } else if (id == R.id.navigation_laundry) {
                replaceFragment(new LaundryFragment());
            } else if (id == R.id.navigation_calendar) {
                replaceFragment(new CalendarFragment());
            } else if (id == R.id.navigation_location) {
                replaceFragment(new LocationFragment());
            }
            return true;
        });
    }

    private void handleNavdrawerClicks(HouseholdViewModel viewModel) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_household) { //Create/Join Household
                Intent intent = new Intent(MainActivity.this, HouseholdActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.navigation_leave_household) { //Leave Household
                showLeaveHouseholdDialog();
            } else if (item.getGroupId() == R.id.group_households) { //Household
                String householdName = item.getTitle().toString();
                viewModel.getHouseholdByName(getApplicationContext(), householdName); // This will update the selectedHousehold LiveData
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.navigation_sign_out) { //Sign Out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    // Override the onOptionsItemSelected() function to implement the item click listener callback
    // to open and close the navigation drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case where the user is not signed in
            Timber.tag(TAG).d("Current user is null. User not signed in.");
            return null;
        }
    }

    private void showLeaveHouseholdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which Household Are You Leaving?");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Household Name");
        builder.setView(input);

        builder.setPositiveButton("Leave", (dialog, which) -> {
            String userId = getCurrentUserId();
            String enteredHouseholdName = input.getText().toString().trim();
            if (!enteredHouseholdName.isEmpty()) {
                Household.leaveHousehold(enteredHouseholdName, userId);
            } else {
                // Show an error message for empty input
                Toast.makeText(this, "Please enter the household name.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showEditDisplayNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Display Name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newDisplayName = input.getText().toString().trim();
            // Update the display name in Firebase and update UI accordingly
            updateDisplayName(newDisplayName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateNavDrawerHeader() {
        // Update the header view and set the user's display name
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textViewDisplayName = headerView.findViewById(R.id.textViewDisplayName);
        SwitchCompat switchDarkMode = navigationView.findViewById(R.id.switch_dark_mode);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is authenticated, set the display name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                textViewDisplayName.setText(displayName);
            } else {
                // Handle the case where display name is null or empty
                textViewDisplayName.setText(R.string.no_display_name);
            }
        }

        // Set OnClickListener to the textViewDisplayName
        textViewDisplayName.setOnClickListener(v -> {
            // Show a dialog to prompt for a new display name
            showEditDisplayNameDialog();
        });

        // Initialize the switch for dark mode
        switchDarkMode.setChecked(isDarkModeEnabled());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateTheme(isChecked);
            Timber.tag(TAG).d("Switch checked: %s", isChecked);
        });
    }

    private void updateDisplayName(String newDisplayName) {
        if (MyApplication.hasNetworkConnection(this)) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Update FirebaseUser's displayName
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newDisplayName)
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                getCurrentUserObject(currentUser.getUid(), user -> {
                                    if (user != null) {
                                        user.setDisplayName(newDisplayName);
                                        updateNavDrawerHeader();
                                    } else {
                                        // Handle null user object
                                        Timber.tag(TAG).e("User object is null");
                                    }
                                });

                            } else {
                                // Handle update failure
                                Timber.tag(TAG).e(task.getException(), "Error updating FirebaseUser's display name");
                            }
                        });
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void applyTheme() {
        // Retrieve the current theme preference
        boolean isDarkModeEnabled = isDarkModeEnabled();
        int themeId = isDarkModeEnabled ? R.style.Theme_Homies_Dark : R.style.Theme_Homies_Light;

        // Apply the theme
        setTheme(themeId);

        // Apply the global theme for the application
        AppCompatDelegate.setDefaultNightMode(
                isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private boolean isDarkModeEnabled() {
        // Retrieve the current theme preference
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        int themeId = preferences.getInt(PREF_THEME_KEY, R.style.Theme_Homies_Light);
        return themeId == R.style.Theme_Homies_Dark;
    }

    private void updateTheme(boolean isDarkModeEnabled) {
        // Determine the new theme
        int newTheme = isDarkModeEnabled ? R.style.Theme_Homies_Dark : R.style.Theme_Homies_Light;

        // Save the selected theme to preferences
        setCurrentTheme(newTheme);

        // Apply the new theme globally
        AppCompatDelegate.setDefaultNightMode(
                isDarkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        // Recreate all activities
        recreate();
    }

    private void setCurrentTheme(int themeId) {
        // Save the selected theme to preferences
        SharedPreferences preferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_THEME_KEY, themeId);
        editor.apply();
    }

    public interface UserCallback {
        void onCallback(User user);
    }

    private void getCurrentUserObject(String userId, UserCallback callback) {
        if (MyApplication.hasNetworkConnection(this)) {
            db = MyApplication.getDbInstance();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            callback.onCallback(user);
                        } else {
                            // Document does not exist
                            Timber.tag(TAG).e("User document not found for user ID: %s", userId);
                            callback.onCallback(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle Firestore query failure
                        Timber.tag(TAG).e(e, "Error fetching user document for user ID: %s", userId);
                        callback.onCallback(null);
                    });
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}