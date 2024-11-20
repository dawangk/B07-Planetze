package com.example.b07projectfall2024;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.b07projectfall2024.NavigationBar.EntryInputs.EntryNavigatorPage;
import com.example.b07projectfall2024.NavigationBar.DashboardFragment;
import com.example.b07projectfall2024.NavigationBar.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for HomeActivity

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Load the default fragment
//        if (savedInstanceState == null) {
//            bottomNavigationView.setSelectedItemId(R.id.dashboard);
//        }
    }

    // Fragments to be used in the BottomNavigationView
    EntryNavigatorPage newEntryFragment = new EntryNavigatorPage(this);
    DashboardFragment dashboardFragment = new DashboardFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Switch fragment based on the selected menu item
        int itemId = item.getItemId();
        if (itemId == R.id.eco_tracker) { // For NewEntryFragment
            selectedFragment = newEntryFragment;
        } else if (itemId == R.id.dashboard) { // For SecondFragment
            selectedFragment = dashboardFragment;
        } else if (itemId == R.id.settings) { // For ThirdFragment
            selectedFragment = settingsFragment;
        }

        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            loadFragment(selectedFragment, true);
            return true;
        }

        return false;
    }

    public void loadFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}