package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class HomeFragment extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_fragment);


        Button buttonRecyclerView = findViewById(R.id.buttonRecyclerView);
        Button buttonScroller = findViewById(R.id.buttonScroller);
        Button buttonSpinner = findViewById(R.id.buttonSpinner);
        Button buttonManageItems = findViewById(R.id.buttonManageItems);
        Button logoutButton = findViewById(R.id.logoutButton); // New Logout Button

        mAuth = FirebaseAuth.getInstance();

        TextView t = findViewById(R.id.NameTest);

        if(UserInformation.getUserInfo() != null){
            t.setText(UserInformation.getUserInfo().getString("name"));
        }

        buttonRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadFragment(new RecyclerViewFragment(), true);
                mAuth.signOut();
                startActivity(new Intent(HomeFragment.this, MainActivity.class));
            }
        });

        buttonScroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ScrollerFragment(), true);
            }
        });

        buttonSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SpinnerFragment(), true);
            }
        });

        buttonManageItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new ManageItemsFragment(), true);}
        });

        // Logout button click listener
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut(); // Sign out from Firebase
            Intent intent = new Intent(HomeFragment.this, LoginActivityView.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Redirect to Login Page
            startActivity(intent);

            // Close current activity
            finish();
        });
    }
    private void loadFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}
