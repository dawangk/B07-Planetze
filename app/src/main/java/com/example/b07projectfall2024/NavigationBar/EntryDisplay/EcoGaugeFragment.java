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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EcoGaugeFragment extends Fragment {

    // Firebase references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = db.getReference();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();

    // Emission variables
    private double totalEmissions;
    private double transportEmissions;
    private double dietEmissions;
    private double consumptionEmissions;

    // UI components
    private TextView totalEmissionsView;
    private String selectedRange = "This Week";

    public EcoGaugeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eco_gauge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize variables
        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        // Bind UI components
        totalEmissionsView = rootView.findViewById(R.id.total_emissions_text);

        // Spinner for time range
        Spinner timeRangeSpinner = rootView.findViewById(R.id.spinner_time_range);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.time_ranges, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeRangeSpinner.setAdapter(adapter);

        // Handle time range selection
        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRange = parent.getItemAtPosition(position).toString();
                calculateEmissionsForRange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
        });

        // Calculate emissions for the default range
        calculateEmissionsForRange();
    }

    private void calculateEmissionsForRange() {
        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        String startDate = getStartDateForRange(selectedRange);
        String endDate = getEndDateForRange();

        ref.child("users").child(user.getUid()).child("entries")
                .orderByKey().startAt(startDate).endAt(endDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                            DatabaseReference transportEntries = dateSnapshot.child("transportation").getRef();
                            DatabaseReference foodEntries = dateSnapshot.child("food").getRef();
                            DatabaseReference consumptionEntries = dateSnapshot.child("consumption").getRef();

                            // Use existing methods to calculate emissions
                            getTransportEmissions(transportEntries, new TextView(requireContext()), totalEmissionsView);
                            getFoodEmissions(foodEntries, new TextView(requireContext()), totalEmissionsView);
                            getConsumptionEmissions(consumptionEntries, new TextView(requireContext()), totalEmissionsView);
                        }

                        // Update total emissions display
                        totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    private String getStartDateForRange(String range) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        switch (range) {
            case "This Week":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case "This Month":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "This Year":
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
        }

        return dateFormat.format(calendar.getTime());
    }

    private String getEndDateForRange() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void getTransportEmissions(DatabaseReference transportEntries, TextView transport_emissions, TextView total_emissions) {

        transportEmissions = 0;
        transport_emissions.setText("Transportation Emissions: 0kg");

        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DatabaseReference transportTypeRef = childSnapshot.getRef().child("TransportationType");
                    String[] transportType = {""};
                    transportTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                transportType[0] = snapshot.getValue(String.class);

                                //Different calculations depending on transportation type
                                switch (transportType[0]) {

                                    case "Car":

                                        DatabaseReference carTypeRef = childSnapshot.getRef().child("CarType");
                                        String[] carType = {""};
                                        carTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    carType[0] = snapshot.getValue(String.class);

                                                    DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Car Emission Rates").child(carType[0]);
                                                    double[] rate = {0};

                                                    rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                rate[0] = snapshot.getValue(Double.class);

                                                                DatabaseReference distanceDrivenRef = childSnapshot.getRef().child("Distance");
                                                                double[] distanceDriven = {0.0};
                                                                distanceDrivenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            distanceDriven[0] = snapshot.getValue(Double.class);

                                                                            transportEmissions += rate[0] * distanceDriven[0];
                                                                            totalEmissions += rate[0] * distanceDriven[0];
                                                                            transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");
                                                                            total_emissions.setText(totalEmissions + "");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    case "Public":

                                        DatabaseReference timeOnPublicRef = childSnapshot.getRef().child("TimeOnPublic");
                                        double[] timeOnPublic = {0.0};
                                        timeOnPublicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    timeOnPublic[0] = snapshot.getValue(Double.class);

                                                    transportEmissions += timeOnPublic[0] * 150;
                                                    transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");
                                                    totalEmissions += timeOnPublic[0] * 150;
                                                    total_emissions.setText(totalEmissions + "");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        break;

                                    case "Plane":

                                        DatabaseReference numFlightsRef = childSnapshot.getRef().child("NmbFlights");
                                        int[] numFlights = {0};
                                        numFlightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    numFlights[0] = snapshot.getValue(Integer.class);

                                                    DatabaseReference flightTypeRef = childSnapshot.getRef().child("FlightType");
                                                    String[] flightType = {""};
                                                    flightTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                flightType[0] = snapshot.getValue(String.class);

                                                                double rate2;

                                                                if (flightType[0].equals("Short-haul (<1,500 km)")) {
                                                                    rate2 = 150;
                                                                }
                                                                else {
                                                                    rate2 = 550;
                                                                }

                                                                transportEmissions += rate2 * numFlights[0];
                                                                transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");
                                                                totalEmissions += rate2 * numFlights[0];
                                                                total_emissions.setText(totalEmissions + "");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getFoodEmissions(DatabaseReference foodEntries, TextView diet_emissions, TextView total_emissions) {

        dietEmissions = 0;
        diet_emissions.setText("Diet Emissions: 0kg");

        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DatabaseReference mealTypeRef = childSnapshot.getRef().child("MealType");
                    String[] mealType = {""};
                    mealTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mealType[0] = snapshot.getValue(String.class);

                                DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Food Emissions Rates").child(mealType[0]);
                                double[] rate = {0};
                                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            rate[0] = snapshot.getValue(Double.class);

                                            DatabaseReference numServingsRef = childSnapshot.getRef().child("NmbConsumedServings");
                                            int[] numServings = {0};
                                            numServingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        numServings[0] = snapshot.getValue(Integer.class);

                                                        dietEmissions += rate[0] * numServings[0];
                                                        diet_emissions.setText("Diet Emissions: " + dietEmissions + "kg");
                                                        totalEmissions += rate[0] * numServings[0];
                                                        total_emissions.setText(totalEmissions + "");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getConsumptionEmissions(DatabaseReference consumptionEntries, TextView consumption_emissions, TextView total_emissions) {

        consumptionEmissions = 0;
        consumption_emissions.setText("Consumptions Emissions: 0kg");

        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DatabaseReference boughtItemRef = childSnapshot.getRef().child("BoughtItem");
                    String[] boughtItem = {""};
                    boughtItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                boughtItem[0] = snapshot.getValue(String.class);

                                switch (boughtItem[0]) {

                                    case "Clothes":

                                        DatabaseReference numClothesRef = childSnapshot.getRef().child("NmbClothingBought");
                                        int[] numClothes = {0};
                                        numClothesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    numClothes[0] = snapshot.getValue(Integer.class);

                                                    consumptionEmissions += 10 * numClothes[0];
                                                    consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                    totalEmissions += 10 * numClothes[0];
                                                    total_emissions.setText(totalEmissions + "");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    case "Electronics":

                                        DatabaseReference numPurchasedRef = childSnapshot.getRef().child("NmbPurchased");
                                        int[] numPurchased = {0};
                                        numPurchasedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    numPurchased[0] = snapshot.getValue(Integer.class);

                                                    DatabaseReference electronicTypeRef = childSnapshot.getRef().child("ElectronicType");
                                                    String[] electronicType = {""};
                                                    electronicTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                electronicType[0] = snapshot.getValue(String.class);

                                                                DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Electronic Emission Rates").child(electronicType[0]);
                                                                int[] rate = {0};
                                                                rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            rate[0] = snapshot.getValue(Integer.class);

                                                                            consumptionEmissions += rate[0] * numPurchased[0];
                                                                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                            totalEmissions += rate[0] * numPurchased[0];
                                                                            total_emissions.setText(totalEmissions + "");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    case "Utility Bill":

                                        DatabaseReference billPriceRef = childSnapshot.getRef().child("BillPrice");
                                        int[] billPrice = {0};
                                        billPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    billPrice[0] = snapshot.getValue(Integer.class);

                                                    DatabaseReference UtilityTypeRef = childSnapshot.getRef().child("UtilityType");
                                                    String[] UtilityType = {""};
                                                    UtilityTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                UtilityType[0] = snapshot.getValue(String.class);

                                                                DatabaseReference rateRef2 = FirebaseDatabase.getInstance().getReference().child("Utility Emission Rates").child(UtilityType[0]);
                                                                double[] rate2 = {0};
                                                                rateRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()) {
                                                                            rate2[0] = snapshot.getValue(Double.class);

                                                                            consumptionEmissions += rate2[0] * billPrice[0];
                                                                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                            totalEmissions += rate2[0] * billPrice[0];
                                                                            total_emissions.setText(totalEmissions + "");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    case "Other":

                                        DatabaseReference numBoughtRef = childSnapshot.getRef().child("NmbPurchased");
                                        int[] numBought = {0};
                                        numBoughtRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    numBought[0] = snapshot.getValue(Integer.class);

                                                    DatabaseReference itemTypeRef = childSnapshot.getRef().child("ItemType");
                                                    String[] itemType = {""};
                                                    itemTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                itemType[0] = snapshot.getValue(String.class);

                                                                int rate3 = 1;
                                                                if (itemType[0].equals("Furniture")) {
                                                                    rate3 = 100;
                                                                } else if (itemType[0].equals("Appliances")) {
                                                                    rate3 = 400;
                                                                }

                                                                consumptionEmissions += rate3 * numBought[0];
                                                                consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                totalEmissions += rate3 * numBought[0];
                                                                total_emissions.setText(totalEmissions + "");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
