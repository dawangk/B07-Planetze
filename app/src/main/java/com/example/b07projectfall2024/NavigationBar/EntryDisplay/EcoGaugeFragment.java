package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.b07projectfall2024.NavigationBar.DashboardFragment;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EcoGaugeFragment extends Fragment {

    private DatabaseReference db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_eco_gauge, container, false);

        // Initialize Firebase
        db = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getUid();

        // Initialize UI elements
        Spinner spinnerTimeRange = view.findViewById(R.id.spinner_time_range);
        TextView tvEmissionsData = view.findViewById(R.id.tv_emissions_data);
        Button backToDashboardButton = view.findViewById(R.id.btn_back_to_dashboard);

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
                fetchEmissionsData(timeRange, tvEmissionsData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvEmissionsData.setText("Select a time range to view emissions");
            }
        });

        // Navigate back to Dashboard
        backToDashboardButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new DashboardFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    /**
     * Fetch emissions data based on the selected time range.
     *
     * @param timeRange The time range selected by the user (e.g., "Last Week").
     * @param tvEmissionsData The TextView to display the emissions data.
     */
    private void fetchEmissionsData(String timeRange, TextView tvEmissionsData) {
        if (userId == null) {
            tvEmissionsData.setText("User not logged in");
            return;
        }

        DatabaseReference entriesRef = db.child("users").child(userId).child("entries");

        switch (timeRange) {
            case "Last Week":
                fetchLastNDaysEmissions(entriesRef, 7, tvEmissionsData, timeRange);
                break;
            case "Last Month":
                fetchLastNDaysEmissions(entriesRef, 30, tvEmissionsData, timeRange);
                break;
            case "Last Year":
                fetchLastNDaysEmissions(entriesRef, 365, tvEmissionsData, timeRange);
                break;
            default:
                tvEmissionsData.setText("No data available.");
                break;
        }
    }

    /**
     * Fetch emissions data for the last N days and calculate the total emissions.
     *
     * @param entriesRef Reference to the user's entries in Firebase.
     * @param days Number of days to fetch data for.
     * @param tvEmissionsData The TextView to display the emissions data.
     * @param timeRange The time range string for the display.
     */
    private void fetchLastNDaysEmissions(DatabaseReference entriesRef, int days, TextView tvEmissionsData, String timeRange) {
        entriesRef.orderByKey().limitToLast(days).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalEmissions = 0.0;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    // Sum emissions for each day
                    totalEmissions += calculateDailyEmissions(dateSnapshot);
                }

                tvEmissionsData.setText("You emitted " + totalEmissions + "kg CO2e in the " + timeRange.toLowerCase() + ".");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvEmissionsData.setText("Failed to retrieve data.");
            }
        });
    }

    /**
     * Calculate the total emissions for a single day based on its entries.
     *
     * @param dateSnapshot The DataSnapshot for a specific day.
     * @return The total emissions for that day.
     */
    private double calculateDailyEmissions(DataSnapshot dateSnapshot) {
        double dailyEmissions = 0.0;

        // Transportation emissions
        if (dateSnapshot.hasChild("transportation")) {
            for (DataSnapshot transport : dateSnapshot.child("transportation").getChildren()) {
                if (transport.hasChild("Distance") && transport.hasChild("CarType")) {
                    double distance = transport.child("Distance").getValue(Double.class);
                    String carType = transport.child("CarType").getValue(String.class);
                    dailyEmissions += getCarEmissionRate(carType) * distance;
                }
            }
        }

        // Food emissions
        if (dateSnapshot.hasChild("food")) {
            for (DataSnapshot food : dateSnapshot.child("food").getChildren()) {
                if (food.hasChild("NmbConsumedServings") && food.hasChild("MealType")) {
                    int servings = food.child("NmbConsumedServings").getValue(Integer.class);
                    String mealType = food.child("MealType").getValue(String.class);
                    dailyEmissions += getFoodEmissionRate(mealType) * servings;
                }
            }
        }

        // Consumption emissions
        if (dateSnapshot.hasChild("consumption")) {
            for (DataSnapshot consumption : dateSnapshot.child("consumption").getChildren()) {
                if (consumption.hasChild("BoughtItem")) {
                    dailyEmissions += calculateConsumptionEmissions(consumption);
                }
            }
        }

        return dailyEmissions;
    }

    /**
     * Fetch the emission rate for a car type.
     *
     * @param carType The type of car (e.g., "Gasoline").
     * @return The emission rate per km for the given car type.
     */
    private double getCarEmissionRate(String carType) {
        switch (carType) {
            case "Gasoline":
                return 2.3;
            case "Diesel":
                return 2.7;
            case "Hybrid":
                return 1.8;
            case "Electric":
                return 0.0;
            default:
                return 0.0;
        }
    }

    /**
     * Fetch the emission rate for a meal type.
     *
     * @param mealType The type of meal (e.g., "Beef").
     * @return The emission rate per serving for the given meal type.
     */
    private double getFoodEmissionRate(String mealType) {
        switch (mealType) {
            case "Beef":
                return 27.0;
            case "Pork":
                return 12.1;
            case "Chicken":
                return 6.9;
            case "Fish":
                return 6.1;
            case "Vegetarian":
                return 2.0;
            default:
                return 0.0;
        }
    }

    /**
     * Calculate emissions from consumption items.
     *
     * @param consumption The DataSnapshot for a consumption entry.
     * @return The emissions for the specific consumption item.
     */
    private double calculateConsumptionEmissions(DataSnapshot consumption) {
        String itemType = consumption.child("BoughtItem").getValue(String.class);
        switch (itemType) {
            case "Clothes":
                int numClothes = consumption.child("NmbClothingBought").getValue(Integer.class);
                return numClothes * 10.0; // Example rate: 10kg CO2 per clothing item
            case "Electronics":
                int numElectronics = consumption.child("NmbPurchased").getValue(Integer.class);
                return numElectronics * 50.0; // Example rate: 50kg CO2 per electronic item
            default:
                return 0.0;
        }
    }
}