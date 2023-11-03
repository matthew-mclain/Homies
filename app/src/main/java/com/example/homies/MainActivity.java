package com.example.homies;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.homies.model.Household;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.example.homies.ui.calendar.CalendarFragment;
import com.example.homies.ui.grocery_list.GroceryListFragment;
import com.example.homies.ui.household.HouseholdActivity;
import com.example.homies.ui.laundry.LaundryFragment;
import com.example.homies.ui.location.LocationFragment;
import com.example.homies.ui.messages.MessagesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;
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
    private static final String TAG = Household.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_messages);
        }

        // Observe the user's households from the ViewModel
        HouseholdViewModel viewModel = new ViewModelProvider(this).get(HouseholdViewModel.class);
        viewModel.getUserHouseholds(getCurrentUserId()).observe(this, households -> {
            // Update the navigation drawer menu with household names
            Timber.tag(TAG).d("Received user households: %s", households);
            Menu navMenu = navigationView.getMenu();

            // Clear the items in the group_households group
            navMenu.removeGroup(R.id.group_households);

            for (Household household : households) {
                Timber.tag(TAG).d("Adding household: %s", household.getHouseholdName());
                MenuItem menuItem = navMenu.add(R.id.group_households, Menu.NONE, Menu.NONE, household.getHouseholdName()).setCheckable(true);
                menuItem.setIcon(R.drawable.household_24);
            }
        });

        // Observe the currently selected household
        viewModel.getSelectedHousehold().observe(this, selectedHousehold -> {
            //TODO: set fragments to display the correct model assigned to the selected household
        });

        //Create new MessagesFragment
        replaceFragment(new MessagesFragment());

        //Handle Navbar and Navdrawer Clicks
        handleNavbarClicks();
        handleNavdrawerClicks(viewModel);

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
            } else if (item.getGroupId() == R.id.group_households) { //Household
                String householdName = item.getTitle().toString();
                viewModel.getHouseholdByName(householdName); // This will update the selectedHousehold LiveData
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
}