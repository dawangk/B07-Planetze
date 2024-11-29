package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EntryFragment extends Fragment {
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    String CurrentSelectedDate;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        // Initialize RecyclerView
        entryRecyclerView = view.findViewById(R.id.entryRecyclerView);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data for the specific date and set up the adapter
        //HashMap<String, HashMap<String, Object>> dataForDate = fetchDataForDate("2024-11-28"); // Replace with actual date

        //entryAdapter = new EntryAdapter(dataForDate);
        //entryRecyclerView.setAdapter(entryAdapter);

        fetchDataForDate("2024-11-28", new DataFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Object>> data) {
                entryAdapter = new EntryAdapter(data);
                entryRecyclerView.setAdapter(entryAdapter);
                Log.d("EntryFragment","Data fetched successfully: " + data);
            }

            @Override
            public void onError(Exception e) {
                Log.e("EntryFragment","Error fetching data: " + e.getMessage());
            }
        });
        return view;
    }
/*
    private HashMap<String, HashMap<String, Object>> fetchDataForDate(String date) {
        // Simulate fetching data for the given date
        HashMap<String, HashMap<String, Object>> entries = new HashMap<>();
        DatabaseReference dayRef = ref.child("users").child(user.getUid()).child("entries").child(date);

        String[] opt = {"transportation", "food", "consumption"};

        for(String s: opt){
            HashMap<String, Object> categoryEntries = new HashMap<>();
            DatabaseReference curRef = dayRef.child(s);
            curRef.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         Log.d("FirebaseTest", "onDataChange() called");
                         if(snapshot.exists()) {
                             for (DataSnapshot child : snapshot.getChildren()) {
                                 String uniq = child.getKey();
                                 HashMap<String, Object> individualEntries = new HashMap<>();
                                 HashMap<String, Object> userEntries = new HashMap<>();

                                 for(Map.Entry<String, Object> entry:userEntries.entrySet()){
                                     Map<String,Object>entryDetails = (Map<String,Object>) entry.getValue();
                                     for(Map.Entry<String, Object> subentry:entryDetails.entrySet()){
                                         individualEntries.put(subentry.getKey(), subentry.getValue());
                                     }
                                     individualEntries.put(entry.getKey(), individualEntries);
                                 }
                                 categoryEntries.put(uniq, individualEntries);
                             }
                         }
                        Log.d("FirebaseTest", "onDataChange() exited");
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {
                     }
                 }

            );
            entries.put(s,categoryEntries);
        }
        return entries;
    }*/
    public interface DataFetchCallback {
        void onSuccess(HashMap<String, HashMap<String, Object>> data);
        void onError(Exception e);
    }
    public void fetchDataForDate(String date, DataFetchCallback callback) {
        // Map to store the entries
        HashMap<String, HashMap<String, Object>> entries = new HashMap<>();
        DatabaseReference dayRef = ref.child("users").child(user.getUid()).child("entries").child(date);

        String[] categories = {"transportation", "food", "consumption"};

        for (String category : categories) {
            DatabaseReference curRef = dayRef.child(category);

            curRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Object> categoryEntries = new HashMap<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            // Unique ID for the entry
                            String uniq = child.getKey();
                            // Data for the individual entry
                            HashMap<String, Object> individualEntry = (HashMap<String, Object>) child.getValue();

                            categoryEntries.put(uniq, individualEntry);
                        }
                    }
                    // Add to the main entries map
                    entries.put(category, categoryEntries);

                    // Check if all categories are loaded
                    if (entries.size() == categories.length) {
                        callback.onSuccess(entries);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onError(error.toException());
                }
            });
        }
    }
}
