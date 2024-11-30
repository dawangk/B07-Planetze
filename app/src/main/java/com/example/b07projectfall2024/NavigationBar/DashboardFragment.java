package com.example.b07projectfall2024.NavigationBar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.b07projectfall2024.HabitTracking.HabitsFragment;
import com.example.b07projectfall2024.NavigationBar.EntryDisplay.EcoGaugeFragment;
import com.example.b07projectfall2024.NavigationBar.EntryDisplay.EntryFragment;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Context;


public class DashboardFragment extends Fragment {

    Context currentContext;
    String CurrentSelectedDate;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    double totalEmissions;
    double transportEmissions;
    double dietEmissions;
    double consumptionEmissions;

    public DashboardFragment() {
        // Required empty public constructor
    }
    TextView total_emissions;
    TextView transport_emissions;
    TextView diet_emissions;
    TextView consumption_emissions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_dashboard, container, false); }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        currentContext = getContext();

        //List of buttons on the dashboard page
        Button dateUpdate = rootView.findViewById(R.id.dateUpdate);
        Button dateViewDetails = rootView.findViewById(R.id.viewDateDetails);
        Button viewHabits = rootView.findViewById(R.id.viewHabits);
        Button btnEcoGauge = rootView.findViewById(R.id.btn_eco_gauge); // New button for Eco Gauge


        TextView dateTextView = rootView.findViewById(R.id.date);
        DateFieldInit(dateTextView);

         total_emissions = rootView.findViewById(R.id.total_emissions_text);
         transport_emissions = rootView.findViewById(R.id.transport_emissions);
         diet_emissions = rootView.findViewById(R.id.diet_emissions);
         consumption_emissions = rootView.findViewById(R.id.consumption_emissions);

         totalEmissions = 0;
         transportEmissions = 0;
         dietEmissions = 0;
         consumptionEmissions = 0;


        //FOR TODAY
        DatabaseReference dayRef = ref.child("users").child(user.getUid()).child("entries").child(CurrentSelectedDate);
        DatabaseReference transportEntries = dayRef.child("transportation");
        DatabaseReference foodEntries = dayRef.child("food");
        DatabaseReference consumptionEntries = dayRef.child("consumption");

        //If entries for the day exist, display. Else, display zero.
        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalEmissions = 0;
                    getTransportEmissions(transportEntries, transport_emissions, total_emissions);
                    getFoodEmissions(foodEntries, diet_emissions, total_emissions);
                    getConsumptionEmissions(consumptionEntries, consumption_emissions, total_emissions);
                }

                else {
                    total_emissions.setText("0");
                    transport_emissions.setText("Transportation Emissions: 0kg");
                    diet_emissions.setText("Diet Emissions: 0kg");
                    consumption_emissions.setText("Consumptions Emissions: 0kg");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        btnEcoGauge.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new EcoGaugeFragment());
            fragmentTransaction.addToBackStack(null); // Allows the user to navigate back
            fragmentTransaction.commit();
        });

        //When selected date is updated, do the same as above
        dateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData();
            }
        });

        dateViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new EntryFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        viewHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,new HabitsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });


    }


    private void UpdateData (){
        DatabaseReference dayRef2 = ref.child("users").child(user.getUid()).child("entries").child(CurrentSelectedDate);

        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        //If entries for the day exist, display. Else, display zero.
        dayRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DatabaseReference transportEntries2 = dayRef2.child("transportation");
                    DatabaseReference foodEntries2 = dayRef2.child("food");
                    DatabaseReference consumptionEntries2 = dayRef2.child("consumption");
                    totalEmissions = 0;
                    getTransportEmissions(transportEntries2, transport_emissions, total_emissions);
                    getFoodEmissions(foodEntries2, diet_emissions, total_emissions);
                    getConsumptionEmissions(consumptionEntries2, consumption_emissions, total_emissions);
                }

                else {
                    total_emissions.setText("0");
                    transport_emissions.setText("Transportation Emissions: 0kg");
                    diet_emissions.setText("Diet Emissions: 0kg");
                    consumption_emissions.setText("Consumptions Emissions: 0kg");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //Initializes DateField allowing users to select any date
    private void DateFieldInit(TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        dateTextView.setText(todayDate);
        CurrentSelectedDate = todayDate;

        dateTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    currentContext,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        dateTextView.setText(selectedDate);
                        CurrentSelectedDate = selectedDate;
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
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

