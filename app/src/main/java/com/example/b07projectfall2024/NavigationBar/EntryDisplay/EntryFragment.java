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
import java.util.LinkedList;
import java.util.Map;

public class EntryFragment extends Fragment {
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    String CurrentSelectedDate;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    private static final String ARG_PARAM = "param_key";

    public static EntryFragment newInstance(String param) {
        EntryFragment fragment = new EntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param); // Add argument to the bundle
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Retrieve arguments
        String date = null;
        if (getArguments() != null) {
            date = getArguments().getString(ARG_PARAM);
        }

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        // Initialize RecyclerView
        entryRecyclerView = view.findViewById(R.id.entryRecyclerView);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchDataForDate(date, new DataFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Object>> data) {
                LinkedList<HashMap<String, Object>> newData = new LinkedList<>();

                for (HashMap.Entry<String, HashMap<String, Object>> entry : data.entrySet()) {
                    String key = entry.getKey();
                    HashMap<String, Object> value = entry.getValue();
                    for(HashMap.Entry<String, Object> entry2: value.entrySet()){
                        HashMap<String, Object> individualEntries = (HashMap<String, Object>)entry2.getValue();
                        HashMap<String, Object> newValue = new HashMap<>(individualEntries);
                        newValue.put("EntryCategory", key);
                        newData.add(newValue);
                    }
                }

                entryAdapter = new EntryAdapter(newData);
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
