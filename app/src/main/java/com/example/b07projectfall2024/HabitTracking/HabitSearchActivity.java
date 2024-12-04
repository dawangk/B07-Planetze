package com.example.b07projectfall2024.HabitTracking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07projectfall2024.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HabitSearchActivity extends AppCompatActivity {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private HabitSearchAdapter habitAdapter;
    private List<Habit> habitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit_search);

       RecyclerView recyclerView = findViewById(R.id.recyclerView);
       SearchView searchView = findViewById(R.id.searchView);

        habitList = new ArrayList<Habit>();
        habitAdapter = new HabitSearchAdapter(habitList, new HabitSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Habit habit) {
                Intent intent = new Intent(HabitSearchActivity.this, HabitProgressActivity.class);
                intent.putExtra("habit", habit.getName());
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        DatabaseReference habitRef = ref.child("Habits");
        //Putting the names of each habit into recyclerView
        habitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                habitList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Habit habit = data.getValue(Habit.class);
                    if (habit != null) {
                        habitList.add(habit);
                    }
                }

                //Adding the names to the recyclerView
                habitAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Filter search results when the user submits a search query
                habitAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Dynamically filter search results while the user is typing
                habitAdapter.filter(newText);
                return false;
            }
        });
    }


}