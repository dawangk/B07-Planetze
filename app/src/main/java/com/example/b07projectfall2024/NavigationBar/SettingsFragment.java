package com.example.b07projectfall2024.NavigationBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Find the Logout Button
        Button logoutButton = view.findViewById(R.id.btn_logout);
        // Find the Dark Mode Switch
        SwitchMaterial darkModeSwitch = view.findViewById(R.id.toggle_dark);


        // Set a click listener for the Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //Set a click listener for the Dark Mode switch
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleMode(isChecked);
            }
        });

        return view;
    }

    private void logout() {
        // Sign out the user from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Redirect to MainActivity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Close the current fragment's parent activity
        requireActivity().finish();
    }

    private void toggleMode(boolean isChecked){
        /*  Checking if the switch is turned on, if on, then switch to night mode,
         *  otherwise, change back to day mode
         */
        if (isChecked) {
            // setting theme to night mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // setting theme to light theme
            AppCompatDelegate.setDefaultNightMode (AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}