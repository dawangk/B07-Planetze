package com.example.b07projectfall2024.HabitTracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotYetTrackedActivity extends AppCompatActivity {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_not_yet_tracked);

        Intent intent = getIntent();
        String habit = intent.getStringExtra("habit");

        TextView habitName = findViewById(R.id.habitName);
        TextView habitDesc = findViewById(R.id.habitDesc);

        Button startTracking = findViewById(R.id.startTracking);
        Button back = findViewById(R.id.back);

        //Setting text fields with habit name and description
        habitName.setText(habit);
        DatabaseReference descRef = ref.child("Habits").child(habit)
                .child("Description");
        descRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String desc = snapshot.getValue(String.class);
                habitDesc.setText(desc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //startTracking should start tracking the habit and redirect to HabitProgressActivity
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Begin tracking
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(habit, 0);
                habitsRef.updateChildren(m);

                //Redirect
                Intent intent = new Intent(NotYetTrackedActivity.this, HabitProgressActivity.class);
                intent.putExtra("habit", habit);
                startActivity(intent);
                finish();
            }
        });

        //back button should redirect back to search
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotYetTrackedActivity.this, HabitSearchActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}