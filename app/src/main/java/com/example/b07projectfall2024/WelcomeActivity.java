package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        Button new_user = findViewById(R.id.new_user);
        Button returning_user = findViewById(R.id.returning_user);

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(WelcomeActivity.this, QuestionnaireActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /** returning_user.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        Intent intent = new Intent
        }
        }); **/

    }
}