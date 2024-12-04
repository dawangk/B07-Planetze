package com.example.b07projectfall2024.NavigationBar;

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

/**
 * DashboardFragment
 * This fragment is responsible for displaying the user's total carbon emissions for any chosen day,
 * and a breakdown of the emissions by the factors of transportation, food and consumption
 */
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

        //Buttons on the dashboard page
        Button dateUpdate = rootView.findViewById(R.id.dateUpdate);
        Button dateViewDetails = rootView.findViewById(R.id.viewDateDetails);
        Button viewHabits = rootView.findViewById(R.id.viewHabits);
        Button btnEcoGauge = rootView.findViewById(R.id.btn_eco_gauge); // Button for Eco Gauge

        //Date selector
        TextView dateTextView = rootView.findViewById(R.id.date);
        DateFieldInit(dateTextView);

        //TextViews for the emission displays
        total_emissions = rootView.findViewById(R.id.total_emissions_text);
        transport_emissions = rootView.findViewById(R.id.transport_emissions);
        diet_emissions = rootView.findViewById(R.id.diet_emissions);
        consumption_emissions = rootView.findViewById(R.id.consumption_emissions);

        //Initializing emission values to 0
        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        //Displaying the current day's emissions
        DatabaseReference dayRef = ref.child("users").child(user.getUid())
                .child("entries").child(CurrentSelectedDate);
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

        //EcoGauge button navigates to EgoGaugeFragment
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

        //ViewDetails button navigates to EntryFragment
        dateViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EntryFragment entryFragment = EntryFragment.newInstance(CurrentSelectedDate);
                fragmentTransaction.replace(R.id.fragment_container, entryFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //viewHabits button navigates to HabitsFragment
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


    /**
     * Updates the dashboard to display the total day emissions of the newly selected date.
     */
    private void UpdateData (){
        DatabaseReference dayRef2 = ref.child("users").child(user.getUid()).
                child("entries").child(CurrentSelectedDate);

        //Initializing emission values to zero
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

    /**
     * Rounds a decimal value to 3 decimal places
     * @param toRound The 'double' value to round
     * @return toRound rounded to 3 decimal places as a 'double'
     */
    private double roundThreeDec(double toRound) {
        return (double)Math.round(toRound * 1000) / 1000;
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

    /**
     * Displays the transportation emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param transportEntries The DatabaseReference where transportation entries of the user are
     *                        stored
     * @param transport_emissions The TextView to which the transport emissions are updated
     * @param total_emissions The TextView to which the total emissions are updated
     */
    private void getTransportEmissions(DatabaseReference transportEntries,
                                       TextView transport_emissions, TextView total_emissions) {

        //Initialize transportation emissions to zero
        transportEmissions = 0;
        transport_emissions.setText("Transportation Emissions: 0kg");

        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DataSnapshot transportTypeRef = childSnapshot.child("TransportationType");
                    String transportType = transportTypeRef.getValue(String.class);

                    //Different calculations depending on transportation type
                    switch (transportType) {

                        case "Car":

                            DataSnapshot carTypeRef = childSnapshot.child("CarType");
                            String carType = carTypeRef.getValue(String.class);

                            //Getting the emission rate of the user's car based on carType
                            DatabaseReference rateRef = db.getReference()
                                    .child("Car Emission Rates").child(carType);
                            double[] rate = {0};

                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Double.class);
                                        DataSnapshot distanceDrivenRef = childSnapshot
                                                .child("Distance");
                                        double distanceDriven = distanceDrivenRef.getValue(Double.class);
                                        transportEmissions = roundThreeDec(transportEmissions + (rate[0] * distanceDriven));
                                        totalEmissions = roundThreeDec(totalEmissions + (rate[0] * distanceDriven));
                                        transport_emissions.setText("Transportation Emissions: " + transportEmissions + "kg");
                                        total_emissions.setText(totalEmissions + "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            break;

                        case "Public":

                            DataSnapshot timeOnPublicRef = childSnapshot.child("TimeOnPublic");
                            double timeOnPublic = timeOnPublicRef.getValue(Double.class);

                            transportEmissions = roundThreeDec(transportEmissions + (timeOnPublic * 150));
                            totalEmissions = roundThreeDec(totalEmissions + (timeOnPublic * 150));
                            transport_emissions.setText("Transportation Emissions: " + transportEmissions + "kg");
                            total_emissions.setText(totalEmissions + "");
                            break;

                        case "Plane":

                            DataSnapshot numFlightsRef = childSnapshot.child("NmbFlights");
                            int numFlights = numFlightsRef.getValue(Integer.class);

                            DataSnapshot flightTypeRef = childSnapshot.child("FlightType");
                            String flightType = flightTypeRef.getValue(String.class);

                            double rate2;

                            if (flightType.equals("Short-haul (<1,500 km)")) {
                                rate2 = 150;
                            } else {
                                rate2 = 550;
                            }

                            transportEmissions = roundThreeDec(transportEmissions + (rate2 * numFlights));
                            totalEmissions = roundThreeDec(totalEmissions + (rate2 * numFlights));
                            transport_emissions.setText("Transportation Emissions: " + transportEmissions + "kg");
                            total_emissions.setText(totalEmissions + "");

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

    /**
     * Displays the diet emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param foodEntries The DatabaseReference where food entries of the user are stored
     * @param diet_emissions The TextView to which the diet emissions are updated
     * @param total_emissions The TextView to which the total emissions are updated
     */
    private void getFoodEmissions(DatabaseReference foodEntries, TextView diet_emissions, TextView total_emissions) {

        //Initialize diet emissions to zero
        dietEmissions = 0;
        diet_emissions.setText("Diet Emissions: 0kg");

        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all food entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DataSnapshot mealTypeRef = childSnapshot.child("MealType");
                    String mealType = mealTypeRef.getValue(String.class);

                    //Getting the emission rate depending on MealType
                    DatabaseReference rateRef = db.getReference().child("Food Emissions Rates").child(mealType);
                    double[] rate = {0};
                    rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                rate[0] = snapshot.getValue(Double.class);
                                DataSnapshot numServingsRef = childSnapshot.child("NmbConsumedServings");
                                int numServings = numServingsRef.getValue(Integer.class);

                                dietEmissions = roundThreeDec(dietEmissions + (rate[0] * numServings));
                                totalEmissions = roundThreeDec(totalEmissions + (rate[0] * numServings));
                                diet_emissions.setText("Diet Emissions: " + dietEmissions + "kg");
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

    /**
     * Displays the consumption emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param consumptionEntries The DatabaseReference where consumption entries of the user are
     *                         stored
     * @param consumption_emissions The TextView to which the consumption emissions are updated
     * @param total_emissions The TextView to which the total emissions are updated
     */
    private void getConsumptionEmissions(DatabaseReference consumptionEntries, TextView consumption_emissions, TextView total_emissions) {

        //Initialize consumption emissions to zero
        consumptionEmissions = 0;
        consumption_emissions.setText("Consumptions Emissions: 0kg");

        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DataSnapshot boughtItemRef = childSnapshot.child("BoughtItem");
                    String boughtItem = boughtItemRef.getValue(String.class);

                    switch (boughtItem) {

                        case "Clothes":

                            DataSnapshot numClothesRef = childSnapshot.child("NmbClothingBought");
                            int numClothes = numClothesRef.getValue(Integer.class);

                            consumptionEmissions = roundThreeDec(consumptionEmissions + (10 * numClothes));
                            totalEmissions = roundThreeDec(totalEmissions + (10 * numClothes));
                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                            total_emissions.setText(totalEmissions + "");

                            break;

                        case "Electronics":

                            DataSnapshot numPurchasedRef = childSnapshot.child("NmbPurchased");
                            int numPurchased = numPurchasedRef.getValue(Integer.class);

                            DataSnapshot electronicTypeRef = childSnapshot.child("ElectronicType");
                            String electronicType = electronicTypeRef.getValue(String.class);

                            //Getting the rate (price) based on electronicType
                            DatabaseReference rateRef = db.getReference().child("Electronic Emission Rates").child(electronicType);
                            int[] rate = {0};

                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Integer.class);
                                        consumptionEmissions = roundThreeDec(consumptionEmissions + (rate[0] * numPurchased));
                                        totalEmissions = roundThreeDec(totalEmissions + (rate[0] * numPurchased));
                                        consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                        total_emissions.setText(totalEmissions + "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            break;

                        case "Utility Bill":

                            DataSnapshot billPriceRef = childSnapshot.child("BillPrice");
                            int billPrice = billPriceRef.getValue(Integer.class);

                            DataSnapshot UtilityTypeRef = childSnapshot.child("UtilityType");
                            String UtilityType = UtilityTypeRef.getValue(String.class);

                            //Getting rate based on UtilityType
                            DatabaseReference rateRef2 = db.getReference().child("Utility Emission Rates").child(UtilityType);
                            double[] rate2 = {0};
                            rateRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate2[0] = snapshot.getValue(Double.class);

                                        consumptionEmissions = roundThreeDec(consumptionEmissions + (rate2[0] * billPrice));
                                        totalEmissions = roundThreeDec(totalEmissions + (rate2[0] * billPrice));
                                        consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                        total_emissions.setText(totalEmissions + "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            break;

                        case "Other":

                            DataSnapshot numBoughtRef = childSnapshot.child("NmbPurchased");
                            int numBought = numBoughtRef.getValue(Integer.class);

                            DataSnapshot itemTypeRef = childSnapshot.child("ItemType");
                            String itemType = itemTypeRef.getValue(String.class);

                            int rate3 = 1;
                            if (itemType.equals("Furniture")) {
                                rate3 = 100;
                            } else if (itemType.equals("Appliances")) {
                                rate3 = 400;
                            }

                            consumptionEmissions = roundThreeDec(consumptionEmissions + (rate3 * numBought));
                            totalEmissions = roundThreeDec(totalEmissions + (rate3 * numBought));
                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                            total_emissions.setText(totalEmissions + "");

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

