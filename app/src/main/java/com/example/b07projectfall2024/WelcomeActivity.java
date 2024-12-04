package com.example.b07projectfall2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.example.b07projectfall2024.RegisterPage.RegisterActivityView;

public class WelcomeActivity extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        if (!isFirstLaunch) {
            startLoginActivityView();
            return;
        }
        
        // Set the content view to the welcome layout
        setContentView(R.layout.activity_welcome);
        
        // Initialize the login button and set a click listener
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the login activity when the login button is clicked
                Intent intent = new Intent(WelcomeActivity.this, LoginActivityView.class);
                startActivity(intent);
                // Finish the current activity so it can't be returned to
                finish();
            }
        });

        // Initialize the register button and set a click listener
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the register activity when the register button is clicked
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivityView.class);
                startActivity(intent);
                // Finish the current activity so it can't be returned to
                finish();
            }
        });
    }

    private void startLoginActivityView() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivityView.class);
        startActivity(intent);
        finish();
    }
}
