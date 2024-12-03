package com.example.b07projectfall2024.HabitTracking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HabitSearchFragment extends Fragment {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public HabitSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_habit_search, container, false);

        SearchView searchHabits = rootView.findViewById(R.id.searchHabits);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HabitSearchAdapter adapter = new HabitSearchAdapter();
        recyclerView.setAdapter(adapter);

        DatabaseReference habitRef = ref.child("Habits");
        //Putting the names of each habit into recyclerView
        habitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> data = new ArrayList<String>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String habitName = childSnapshot.child("Name").getValue(String.class);
                    data.add(habitName);
                }

                //Adding the names to the recyclerView
                adapter.updateData(data);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchHabits.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Filter search results when the user submits a search query
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Dynamically filter search results while the user is typing
                adapter.filter(newText);
                return false;
            }
        });

        //ADD ON CLICK FOR ADAPTER for when a search result is clicked

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

    }
}