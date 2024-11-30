package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EcoGaugeFragment extends Fragment {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private TextView tvEmissionsData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_eco_gauge, container, false);

        Spinner spinnerTimeRange = view.findViewById(R.id.spinner_time_range);
        tvEmissionsData = view.findViewById(R.id.tv_emissions_data);

        // Set up the dropdown menu (Spinner)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.time_range_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeRange.setAdapter(adapter);

        // Handle selection from the dropdown menu
        spinnerTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeRange = parent.getItemAtPosition(position).toString();
                fetchEmissionsData(timeRange); // Fetch data from Firebase
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvEmissionsData.setText("Select a time range to view emissions");
            }
        });

        return view;
    }

    // Fetch emissions data from Firebase
    private void fetchEmissionsData(String timeRange) {
        if (user == null) {
            tvEmissionsData.setText("User not authenticated");
            return;
        }

        DatabaseReference userRef = dbRef.child("users").child(user.getUid()).child("entries");
        // Define time range logic
        switch (timeRange) {
            case "Last Week":
                fetchDataForRange(userRef, 7);
                break;
            case "Last Month":
                fetchDataForRange(userRef, 30);
                break;
            case "Last Year":
                fetchDataForRange(userRef, 365);
                break;
            default:
                tvEmissionsData.setText("No data available.");
        }
    }

    private void fetchDataForRange(DatabaseReference userRef, int days) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalEmissions = 0;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    // Ensure dates within the desired range are processed
                    // Parse the dates and implement filtering logic here if necessary

                    for (DataSnapshot categorySnapshot : dateSnapshot.getChildren()) {
                        for (DataSnapshot entrySnapshot : categorySnapshot.getChildren()) {
                            Object value = entrySnapshot.child("emissions").getValue(); // Assuming emissions stored as "emissions"
                            if (value != null) {
                                totalEmissions += Double.parseDouble(value.toString());
                            }
                        }
                    }
                }
                tvEmissionsData.setText("Total Emissions: " + totalEmissions + "kg CO2e");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvEmissionsData.setText("Error fetching data.");
            }
        });
    }
}