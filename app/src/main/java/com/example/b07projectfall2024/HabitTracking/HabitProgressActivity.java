package com.example.b07projectfall2024.HabitTracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07projectfall2024.R;

public class HabitProgressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit_progress);

        Intent intent = getIntent();
        String habit = intent.getStringExtra("habit");

        TextView title = findViewById(R.id.title);
        title.setText("Your progress towards " + habit + ":");
    }
}