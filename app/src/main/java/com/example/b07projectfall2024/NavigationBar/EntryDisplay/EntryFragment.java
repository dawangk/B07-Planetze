package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07projectfall2024.NavigationBar.EntryInputs.ConsumptionEntry;
import com.example.b07projectfall2024.NavigationBar.EntryInputs.FoodEntryPage;
import com.example.b07projectfall2024.NavigationBar.EntryInputs.TransportEntryPage;
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

public class EntryFragment extends Fragment implements EntryAdapter.OnItemClickListener{
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    LinkedList<HashMap<String, Object>> dataList;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    boolean shouldDeleteOnReturn = false;
    private static final String ARG_PARAM = "param_key";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            shouldDeleteOnReturn = savedInstanceState.getBoolean("shouldDeleteOnReturn", false);
            if (savedInstanceState.containsKey("position")) {
                Bundle args = getArguments();
                if (args == null) {
                    args = new Bundle();
                }
                args.putInt("position", savedInstanceState.getInt("position"));
                setArguments(args); // Restore position to arguments
            }
        }
    }
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

        updateData(date);

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
                    Log.d("EntryFragment", "Entries for " + category + ": " + categoryEntries);

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

    @Override
    public void onEditClick(int position) {
        String date = null;
        if (getArguments() != null) {
            date = getArguments().getString(ARG_PARAM);
        }

        HashMap<String, Object> item = dataList.get(position);

        DatabaseReference dayRef = ref.child("users").child(user.getUid()).child("entries").child(date);
        String category = (String) item.get("EntryCategory");

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Find the existing fragment you want to navigate to (e.g., TransportEntryPage)
        Fragment editFragment = null;

        if(category.equals("transportation")){
            editFragment =  new TransportEntryPage(date);
        }else if(category.equals("food")){
            editFragment = new FoodEntryPage(date);
        }else if(category.equals("consumption")){
            editFragment = new ConsumptionEntry(date);
        }

        // Pass the position to the new fragment
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        editFragment.setArguments(bundle);

        // Also save the position in the current fragment
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putInt("position", position);
        setArguments(args);

        // Replace the current fragment with the edit entries fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EDIT_FRAGMENT")
                .addToBackStack(null) // Add to the back stack so the user can navigate back
                .commit();

        shouldDeleteOnReturn = true;
        updateData(date);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("shouldDeleteOnReturn", shouldDeleteOnReturn);
        if (getArguments() != null && getArguments().containsKey("position")) {
            outState.putInt("position", getArguments().getInt("position"));
        }
    }

    // Handle deletion when returning to this fragment
    @Override
    public void onResume() {
        String date = getArguments() != null ? getArguments().getString(ARG_PARAM) : null;
        super.onResume();
        if (shouldDeleteOnReturn) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("position")) {
                int position = args.getInt("position");
                onDeleteClick(position, true);
            }
            shouldDeleteOnReturn = false;
            updateData(date);
            replaceFragmentWithNewInstance(date);
        }
    }


    @Override
    public void onDeleteClick(int position, boolean edit) {
        String date = getArguments() != null ? getArguments().getString(ARG_PARAM) : null;

        HashMap<String, Object> item = dataList.get(position);
        String category = (String) item.get("EntryCategory");
        String id = (String) item.get("ID");
        DatabaseReference entryRef = ref.child("users").child(user.getUid()).child("entries").child(date).child(category).child(id);

        entryRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (edit) {
                    Toast.makeText(getContext(), "Entry edited successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Entry removed successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("FirebaseDB", "Failed to remove entry: ", task.getException());
            }
            updateData(date);
        });
    }


    private void updateData(String date) {
        fetchDataForDate(date, new DataFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Object>> data) {
                LinkedList<HashMap<String, Object>> newData = new LinkedList<>();
                for (HashMap.Entry<String, HashMap<String, Object>> entry : data.entrySet()) {
                    String key = entry.getKey();
                    HashMap<String, Object> value = entry.getValue();
                    for (HashMap.Entry<String, Object> entry2 : value.entrySet()) {
                        HashMap<String, Object> individualEntries = (HashMap<String, Object>) entry2.getValue();
                        HashMap<String, Object> newValue = new HashMap<>(individualEntries);
                        newValue.put("EntryCategory", key);
                        newValue.put("ID", entry2.getKey());
                        newData.add(newValue);
                    }
                }
                dataList = newData;
                if (entryAdapter == null) {
                    entryAdapter = new EntryAdapter(newData, EntryFragment.this);
                    entryRecyclerView.setAdapter(entryAdapter);
                } else {
                    entryAdapter.updateData(newData);
                    entryAdapter.notifyDataSetChanged();
                }
                Log.d("EntryFragment", "Data fetched successfully: " + newData);
            }

            @Override
            public void onError(Exception e) {
                Log.e("EntryFragment", "Error fetching data: " + e.getMessage());
            }
        });
    }

    public void replaceFragmentWithNewInstance(String date) {
        // Create a new instance of EntryFragment
        EntryFragment newFragment = EntryFragment.newInstance(date);

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a transaction to replace the current fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)  // The container where the fragment should be replaced
                .addToBackStack(null)  // Optional: Adds the transaction to the back stack
                .commit();
    }
}
