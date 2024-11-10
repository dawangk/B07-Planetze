package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);

//        myRef.setValue("B07 Demo!");

//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();

        if (savedInstanceState == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }else{
            startActivity(new Intent(MainActivity.this, HomeFragment.class));
            finish();
        }
    }

    private void loadFragment(Fragment fragment, boolean addToStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToStack) transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int stackCount = getSupportFragmentManager().getBackStackEntryCount();
        if (stackCount > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}