package com.example.b07projectfall2024.NavigationBar.EntryDisplay;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EcoGaugeFragment extends Fragment {

    // Firebase references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = db.getReference();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();

    // Emission-related variables for calculations
    private double totalEmissions;
    private double transportEmissions;
    private double dietEmissions;
    private double consumptionEmissions;

    // UI components
    private TextView totalEmissionsView;
    private String selectedRange = "This Week";

    private LineChart chart;

    // UI components for country selection
    private Spinner countrySpinner;
    private TextView countryEmissionsText;
    private final DatabaseReference countriesRef = ref.child("Countries");

    public EcoGaugeFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eco_gauge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Bind UI components to variables
        totalEmissionsView = rootView.findViewById(R.id.total_emissions_text);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        countrySpinner = rootView.findViewById(R.id.spinner_country);
        countryEmissionsText = rootView.findViewById(R.id.country_emissions_text);

        // Initialize a default entry to format the chart correctly
        entryEmissions = new HashMap<>();
        entryEmissions.put("2024-12-01", new TotalEntryEmission(0,0,0));
        updateLineGraph();

        // Set up spinner for time range selection (e.g., This Week, This Month)
        Spinner timeRangeSpinner = rootView.findViewById(R.id.spinner_time_range);
        ArrayAdapter<CharSequence> timeRangeAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.time_ranges, android.R.layout.simple_spinner_item);
        timeRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeRangeSpinner.setAdapter(timeRangeAdapter);

        // Handle time range selection
        timeRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRange = parent.getItemAtPosition(position).toString();
                calculateEmissionsForRange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required when nothing is selected
            }
        });

        // Set up the country spinner for comparing emissions
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.countries, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Handle country selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedCountry = parent.getItemAtPosition(position).toString();
                    fetchCountryEmissions(selectedCountry);
                }
                else {
                    countryEmissionsText.setText("Select a country to see its average emissions.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    // Class to store daily emissions by category
    private class TotalEntryEmission{
        double transportEmission;
        double foodEmission;
        double consumptionEmission;

        // Constructor to initialize emissions
        public TotalEntryEmission(double transportEmission, double foodEmission, double consumptionEmission){
            this.transportEmission=transportEmission;
            this.foodEmission=foodEmission;
            this.consumptionEmission=consumptionEmission;
        }
    }

    // HashMap to store emissions data for charting
    private HashMap<String, TotalEntryEmission> entryEmissions;

    // Method to update the line chart with current emissions data
    private void updateLineGraph(){
        if(entryEmissions.isEmpty()) return;

        ArrayList<Entry> lineGraphPoints = new ArrayList<Entry>();
        List<String> dateLabels = new ArrayList<>(entryEmissions.keySet());
        Collections.sort(dateLabels);
        List<String> dates = new ArrayList<>();

        // Define the SimpleDateFormat for the date string format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String curDate = dateLabels.get(0); // Start with the first date
        dates.add(curDate); // Add it to the list

        // Generate list of consecutive dates
        while(!curDate.equals(dateLabels.get(dateLabels.size()-1))){
            try {
                // Convert string to Date
                Date startDate = sdf.parse(curDate);

                // Create a Calendar instance and set the time to the start date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);

                // Increment day
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                Date newDate = calendar.getTime();

                // Format the new date
                curDate = sdf.format(newDate);
                dates.add(curDate);
            }

            catch (java.text.ParseException e) {
                throw new RuntimeException(e);
            }
        }

        // Populate chart data points
        int index = 0;
        for (String date : dates) {
            if(entryEmissions.containsKey(date)) {
                lineGraphPoints.add(new Entry(index, (float) (entryEmissions.get(date).consumptionEmission+entryEmissions.get(date).transportEmission+entryEmissions.get(date).foodEmission)));
            }
            index++;
        }

        // Create and style the LineDataSet
        LineDataSet dataSet = new LineDataSet(lineGraphPoints, "Total Emissions");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setFormLineWidth(20f);
        dataSet.setValueTextSize(0f);

        chart.getDescription().setEnabled(false);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Customize legend and axes
        Legend legend = chart.getLegend();
        legend.setTextSize(18f);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setTextSize(18f);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);

        // Customize X-axis to display dates
        XAxis xAxis = chart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(18f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Set granularity to 1 step
        if(dates.size()>=2)xAxis.setLabelCount(2, true);

        // Custom formatter for date labels
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int)value;
                if(0<=value && value< dates.size()){
                    return dates.get(idx).substring(5);
                }
                return "";
            }
        });

        // Refresh the chart
        chart.invalidate();
    }

    // Helper method to add or update transportation emissions
    private void putTransportEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            // If the date exists, add the new emission to the existing transportation value
            entryEmissions.get(date).transportEmission+=emission;
        }
        else{
            // If the date doesn't exist, create a new TotalEntryEmission object with the given transportation emission
            entryEmissions.put(date, new TotalEntryEmission(emission, 0, 0));
        }
        // Update the line chart with the latest data
        updateLineGraph();
    }

    // Helper method to add or update food emissions for a given date
    private void putFoodEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            // If the date exists, add the new emission to the existing food value
            entryEmissions.get(date).foodEmission+=emission;
        }
        else{
            // If the date doesn't exist, create a new TotalEntryEmission object with the given food emission
            entryEmissions.put(date, new TotalEntryEmission(0, emission, 0));
        }
        // Update the line chart with the latest data
        updateLineGraph();
    }

    // Helper method to add or update consumption emissions for a given date
    private void putConsumptionEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            // If the date exists, add the new emission to the existing consumption value
            entryEmissions.get(date).consumptionEmission+=emission;
        }
        else{
            // If the date doesn't exist, create a new TotalEntryEmission object with the given consumption emission
            entryEmissions.put(date, new TotalEntryEmission(0, 0, emission));
        }
        // Update the line chart with the latest data
        updateLineGraph();
    }

    // Method to calculate emissions for the selected time range
    private void calculateEmissionsForRange() {
        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        entryEmissions = new HashMap<>();

        // Get start and end dates based on the selected range
        String startDate = getStartDateForRange(selectedRange);
        String endDate = getEndDateForRange();

        // Fetch data from Firebase for the specified date range
        ref.child("users").child(user.getUid()).child("entries")
                .orderByKey().startAt(startDate).endAt(endDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Iterate over each date entry in the snapshot
                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                            String Date = dateSnapshot.getKey();

                            // Fetch category-specific data references
                            DatabaseReference transportEntries = dateSnapshot.child("transportation").getRef();
                            DatabaseReference foodEntries = dateSnapshot.child("food").getRef();
                            DatabaseReference consumptionEntries = dateSnapshot.child("consumption").getRef();

                            // Calculate emissions for each category
                            getTransportEmissions(transportEntries, new TextView(requireContext()), Date);
                            getFoodEmissions(foodEntries, new TextView(requireContext()), Date);
                            getConsumptionEmissions(consumptionEntries, new TextView(requireContext()), Date);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    // Determines the start date based on the selected time range (e.g., "This Week", "This Month", "This Year")
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

    // Method to get the current date (end date) for the selected range
    private String getEndDateForRange() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Method to calculate transportation emissions for a specific date
    private void getTransportEmissions(DatabaseReference transportEntries, TextView transport_emissions, String Date) {
        transport_emissions.setText("Transportation Emissions: 0kg");

        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    // Reference the type of transportation
                    DatabaseReference transportTypeRef = childSnapshot.getRef().child("TransportationType");
                    String[] transportType = {""};

                    // Fetch the transportation type (e.g., Car, Plane, Public)
                    transportTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                transportType[0] = snapshot.getValue(String.class);

                                // Calculate emissions based on the type of transportation
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
                                                                            double tmpEmissions = rate[0] * distanceDriven[0];
                                                                            putTransportEntry(tmpEmissions, Date);

                                                                            transportEmissions += tmpEmissions;
                                                                            totalEmissions += tmpEmissions;

                                                                            transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");
                                                                            totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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
                                                    double tmpEmissions = timeOnPublic[0] * 150;

                                                    putTransportEntry(tmpEmissions, Date);

                                                    transportEmissions += tmpEmissions;
                                                    totalEmissions += tmpEmissions;

                                                    transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");
                                                    totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

                                                                double tmpEmissions = rate2 * numFlights[0];

                                                                putTransportEntry(tmpEmissions, Date);

                                                                transportEmissions += tmpEmissions;
                                                                transport_emissions.setText("Transportation Emissions: " +  transportEmissions + "kg");

                                                                totalEmissions += tmpEmissions;
                                                                totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

    // Method to calculate food emissions for a specific date
    private void getFoodEmissions(DatabaseReference foodEntries, TextView diet_emissions, String Date) {
        diet_emissions.setText("Diet Emissions: 0kg");

        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    // Reference the meal type (e.g., Vegetarian, Meat)
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
                                                        double tmpEmissions = rate[0] * numServings[0];

                                                        putFoodEntry(tmpEmissions, Date);

                                                        dietEmissions += tmpEmissions;
                                                        diet_emissions.setText("Diet Emissions: " + dietEmissions + "kg");
                                                        totalEmissions += tmpEmissions;
                                                        // Update total emissions display
                                                        totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

    // Method to calculate consumption emissions for a specific date
    private void getConsumptionEmissions(DatabaseReference consumptionEntries, TextView consumption_emissions, String Date) {
        consumption_emissions.setText("Consumptions Emissions: 0kg");

        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    // Reference the type of purchased item (e.g., Clothes, Electronics, Utility Bill)
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

                                                    double tmpEmission = 10 * numClothes[0];

                                                    putConsumptionEntry(tmpEmission, Date);

                                                    consumptionEmissions += tmpEmission;
                                                    consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                    totalEmissions += tmpEmission;
                                                    // Update total emissions display
                                                    totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

                                                                            double tmpEmissions = rate[0] * numPurchased[0];

                                                                            putConsumptionEntry(tmpEmissions, Date);

                                                                            consumptionEmissions += tmpEmissions;
                                                                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                            totalEmissions += tmpEmissions;
                                                                            // Update total emissions display
                                                                            totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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
                                                                            double tmpEmissions = rate2[0] * billPrice[0];

                                                                            putConsumptionEntry(tmpEmissions, Date);

                                                                            consumptionEmissions += tmpEmissions;
                                                                            consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                            totalEmissions += tmpEmissions;
                                                                            // Update total emissions display
                                                                            totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

                                                                double tmpEmissions = rate3 * numBought[0];

                                                                putConsumptionEntry(tmpEmissions, Date);

                                                                consumptionEmissions += tmpEmissions;
                                                                consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
                                                                totalEmissions += tmpEmissions;
                                                                // Update total emissions display
                                                                totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
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

    // Fetches and displays average emissions data for a selected country
    private void fetchCountryEmissions(String country) {
        if (country == null || country.isEmpty()) {
            countryEmissionsText.setText("Please select a valid country.");
            return;
        }

        countriesRef.child(country.trim()).child("total_emissions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {

                            // Fetch yearly emissions
                            double yearlyEmissions = snapshot.getValue(Double.class);

                            // Calculate monthly and weekly averages
                            double monthlyEmissions = yearlyEmissions / 12.0;
                            double weeklyEmissions = yearlyEmissions / 52.0;
                            requireActivity().runOnUiThread(() -> {
                                String emissionsText = String.format(Locale.getDefault(),
                                        "Average emissions for %s :\n\n" +
                                                "Yearly : %.1f kg CO2e\n" +
                                                "Monthly : %.1f kg CO2e\n" +
                                                "Weekly : %.1f kg CO2e",
                                        country, yearlyEmissions, monthlyEmissions, weeklyEmissions);

                                countryEmissionsText.setText(emissionsText);
                            });
                        }

                        else {
                            requireActivity().runOnUiThread(() -> {
                                countryEmissionsText.setText(String.format("No data available for %s.", country));
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        requireActivity().runOnUiThread(() -> {
                            countryEmissionsText.setText("Failed to fetch data. Please try again.");
                        });
                    }
                });
    }

}
