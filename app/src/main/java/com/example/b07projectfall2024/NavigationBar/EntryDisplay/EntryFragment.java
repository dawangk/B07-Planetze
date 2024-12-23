package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.os.Bundle;
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

public class EntryFragment extends Fragment implements EntryAdapter.OnItemClickListener {
    private RecyclerView entryRecyclerView;
    private EntryAdapter entryAdapter;
    LinkedList<HashMap<String, Object>> dataList;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    boolean shouldDeleteOnReturn = false;
    private static final String ARG_PARAM = "param_key";

    // onCreate method: called when the fragment is first created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore saved state if available
        if (savedInstanceState != null) {
            shouldDeleteOnReturn = savedInstanceState.getBoolean("shouldDeleteOnReturn",
                    false);
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

    // Factory method to create a new instance of this fragment with a parameter
    public static EntryFragment newInstance(String param) {
        EntryFragment fragment = new EntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param); // Add argument to the bundle
        fragment.setArguments(args);
        return fragment;
    }

    // onCreateView: Inflates the layout for the fragment and sets up the RecyclerView
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Retrieve arguments
        String date = null;
        if (getArguments() != null) {
            date = getArguments().getString(ARG_PARAM);
        }

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        // Initialize RecyclerView and set its layout manager
        entryRecyclerView = view.findViewById(R.id.entryRecyclerView);
        entryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Update the data to display
        updateData(date);

        return view;
    }

    // Callback interface to handle success or error in data fetching
    public interface DataFetchCallback {
        void onSuccess(HashMap<String, HashMap<String, Object>> data);
        void onError(Exception e);
    }

    // Fetches data for a specific date and passes it to the callback
    public void fetchDataForDate(String date, DataFetchCallback callback) {
        HashMap<String, HashMap<String, Object>> entries = new HashMap<>();
        DatabaseReference dayRef = ref.child("users")
                .child(user.getUid()).child("entries").child(date);

        String[] categories = {"transportation", "food", "consumption"};

        // For each category (transportation, food, consumption), fetch the data
        for (String category : categories) {
            DatabaseReference curRef = dayRef.child(category);

            curRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String, Object> categoryEntries = new HashMap<>();
                    if (snapshot.exists()) {
                        // Add each child entry under the category to the map
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String uniq = child.getKey();
                            HashMap<String, Object> individualEntry =
                                    (HashMap<String, Object>) child.getValue();
                            categoryEntries.put(uniq, individualEntry);
                        }
                    }
                    // Add this category's entries to the main entries map
                    entries.put(category, categoryEntries);
                    Log.d("EntryFragment", "Entries for " +
                            category + ": " + categoryEntries);

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

    // onEditClick: Handles the logic when the Edit button is clicked
    @Override
    public void onEditClick(int position) {
        String date = null;
        if (getArguments() != null) {
            date = getArguments().getString(ARG_PARAM);
        }

        HashMap<String, Object> item = dataList.get(position);
        DatabaseReference dayRef = ref.child("users").child(user.getUid())
                .child("entries").child(date);
        String category = (String) item.get("EntryCategory");

        // Get the FragmentManager for fragment navigation
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment editFragment = null;

        // Determine the correct fragment to navigate to based on category
        if (category.equals("transportation")) {
            editFragment = new TransportEntryPage(date);
        } else if (category.equals("food")) {
            editFragment = new FoodEntryPage(date);
        } else if (category.equals("consumption")) {
            editFragment = new ConsumptionEntry(date);
        }

        // Pass the position to the new fragment
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        editFragment.setArguments(bundle);

        // Update the current fragment's arguments with the position
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putInt("position", position);
        setArguments(args);

        // Replace the current fragment with the edit fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editFragment, "EDIT_FRAGMENT")
                .addToBackStack(null) // Add to back stack for easy navigation back
                .commit();

        // Flag that we should delete the entry when returning
        shouldDeleteOnReturn = true;
        updateData(date);
    }

    // onSaveInstanceState: Save the state of the fragment when it's destroyed
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("shouldDeleteOnReturn", shouldDeleteOnReturn);
        if (getArguments() != null && getArguments().containsKey("position")) {
            outState.putInt("position", getArguments().getInt("position"));
        }
    }

    // onResume: Handles the logic for deletion when returning to the fragment
    @Override
    public void onResume() {
        String date = getArguments() != null ? getArguments().getString(ARG_PARAM) : null;
        super.onResume();
        if (shouldDeleteOnReturn) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("position")) {
                int position = args.getInt("position");
                onDeleteClick(position, true); // Delete the entry if necessary
            }
            shouldDeleteOnReturn = false;
            updateData(date);
            replaceFragmentWithNewInstance(date); // Replace the fragment with a new instance
        }
    }

    // onDeleteClick: Handles the deletion of an entry when the delete button is clicked
    @Override
    public void onDeleteClick(int position, boolean edit) {
        String date = getArguments() != null ? getArguments().getString(ARG_PARAM) : null;

        HashMap<String, Object> item = dataList.get(position);
        String category = (String) item.get("EntryCategory");
        String id = (String) item.get("ID");
        DatabaseReference entryRef = ref.child("users").child(user.getUid())
                .child("entries").child(date).child(category).child(id);

        // Remove the entry from the database
        entryRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Show toast depending on whether it's an edit or a delete
                if (edit) {
                    Toast.makeText(getContext(), "Entry edited successfully",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Entry removed successfully",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("FirebaseDB", "Failed to remove entry: ", task.getException());
            }
            updateData(date);
        });
    }

    // updateData: Fetches and updates the data displayed in the RecyclerView
    private void updateData(String date) {
        fetchDataForDate(date, new DataFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Object>> data) {
                //Data reprocessing, converting form HashMap into a LinkedList
                LinkedList<HashMap<String, Object>> newData = new LinkedList<>();
                for (HashMap.Entry<String, HashMap<String, Object>> entry : data.entrySet()) {
                    String key = entry.getKey();
                    HashMap<String, Object> value = entry.getValue();
                    for (HashMap.Entry<String, Object> entry2 : value.entrySet()) {
                        HashMap<String, Object> individualEntries = (HashMap<String, Object>)
                                entry2.getValue();
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
    // replaceFragmentWithNewInstance: reloads a new instance of this fragment
    public void replaceFragmentWithNewInstance(String date) {
        // Create a new instance of EntryFragment
        EntryFragment newFragment = EntryFragment.newInstance(date);

        // Get the FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Start a transaction to replace the current fragment
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit();
    }
}
