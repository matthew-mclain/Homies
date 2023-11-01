package com.example.homies;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.homies.ui.calendar.CalendarFragment;
import com.example.homies.ui.grocery_list.GroceryListFragment;
import com.example.homies.ui.household.HouseholdActivity;
import com.example.homies.ui.laundry.LaundryFragment;
import com.example.homies.ui.location.LocationFragment;
import com.example.homies.ui.messages.MessagesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public BottomNavigationView bottomNavigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_messages);
        }

        //Create new MessagesFragment
        replaceFragment(new MessagesFragment());

        //Handle Navbar clicks
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

        //Handle Navdrawer clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_household) {
                Intent intent = new Intent(MainActivity.this, HouseholdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (id == R.id.navigation_sign_out) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    // override the onOptionsItemSelected() function to implement the item click listener callback
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
}