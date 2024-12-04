package com.example.b07projectfall2024.NavigationBar.EntryDisplay;
import com.example.b07projectfall2024.NavigationBar.DailyEmissionGetterReceiver;
import com.example.b07projectfall2024.NavigationBar.DailyEmissionsServer;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

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

public class EcoGaugeFragment extends Fragment implements DailyEmissionGetterReceiver {

    // Firebase references
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = db.getReference();

    // Emission variables
    private double totalEmissions;

    // UI components
    private TextView totalEmissionsView;
    private String selectedRange = "This Week";

    private BarChart barChart;
    private LineChart EmissionLineChart;

    private DailyEmissionsServer EmissionsServer;
    private TextView countryEmissionsText;
    private final DatabaseReference countriesRef = ref.child("Countries");

    public EcoGaugeFragment(){
        EmissionsServer = new DailyEmissionsServer(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eco_gauge, container, false);
    }

    /*
        Cycle renders all chart to ensure data is properly displayed
     */
    private void CycleEmissionCharts(){
        entryEmissions = new HashMap<>();
        entryEmissions.put("2024-12-01", new TotalEntryEmission(0,0,0));
        updateLineChart();
        updateBarChart();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Bind UI components
        totalEmissionsView = rootView.findViewById(R.id.total_emissions_text);

        EmissionLineChart = rootView.findViewById(R.id.chart);
        barChart = (BarChart) rootView.findViewById(R.id.bar_chart);

        Spinner countrySpinner = rootView.findViewById(R.id.spinner_country);
        countryEmissionsText = rootView.findViewById(R.id.country_emissions_text);

        CycleEmissionCharts();

        // Spinner for time range
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
                EmissionsServer.calculateEmissionsForRange(getStartDateForRange(selectedRange), getEndDateForRange());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required
            }
        });

        // Set up the country spinner
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.countries, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Handle country selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Skip the placeholder
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

    private class TotalEntryEmission{
        double transportEmission;
        double foodEmission;
        double consumptionEmission;

        public TotalEntryEmission(double transportEmission, double foodEmission, double consumptionEmission){
            this.transportEmission=transportEmission;
            this.foodEmission=foodEmission;
            this.consumptionEmission=consumptionEmission;
        }
    }

    //Key: Date (YYYY-MM-DD)
    private HashMap<String, TotalEntryEmission> entryEmissions;

    /*
        Given a start and end date in format (YYYY-MM-DD)
        return a list of all dates between the start and end date inclusive in
        chronological order

        if the given start or end date is not formatted properly throw runtimeexception
     */
    private List<String> getListOfDaysBetween(String start, String end){
        List<String> dates = new ArrayList<>();

        // Define the SimpleDateFormat for the date string format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String curDate = start;

        dates.add(curDate);

        //check if end date is formatted properly
        try{
            sdf.parse(end);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }

        while(!curDate.equals(end)){
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
            } catch (java.text.ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return dates;
    }

    /*
        Initializes the given LineDataSets settings

        Adds labels to the xAxis with the given dates
     */
    private void InitLineChartSettings(LineDataSet dataSet, List<String> dates){
        //LineChart Settings:
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setFormLineWidth(20f);
        dataSet.setValueTextSize(0f);

        EmissionLineChart.getDescription().setEnabled(false);

        LineData lineData = new LineData(dataSet);
        EmissionLineChart.setData(lineData);

        Legend legend = EmissionLineChart.getLegend();
        legend.setTextSize(18f);

        YAxis yAxis = EmissionLineChart.getAxisLeft();
        yAxis.setTextSize(18f);

        YAxis rightYAxis = EmissionLineChart.getAxisRight();
        rightYAxis.setEnabled(false);

        // Customize X-Axis to show dates
        XAxis xAxis = EmissionLineChart.getXAxis();
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTextSize(18f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        if(dates.size()>=2)xAxis.setLabelCount(2, true);

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
    }

    /*
        Given Data in entryEmissions, update the emission line chart to reflect changes made

        If no data is given in entryEmissions then don't re-render.
     */
    private void updateLineChart(){
        if(entryEmissions.isEmpty()) return;

        //Get and sort dates in entryEmissions
        List<String> dateLabels = new ArrayList<>(entryEmissions.keySet());
        Collections.sort(dateLabels);

        List<String> dates = getListOfDaysBetween(dateLabels.get(0),
                                                  dateLabels.get(dateLabels.size()-1));

        //Add datapoints given in entryEmissions into the linechart datapoint entry list
        ArrayList<Entry> lineGraphPoints = new ArrayList<Entry>();
        int index = 0;
        for (String date : dates) {
            if(entryEmissions.containsKey(date)) {
                TotalEntryEmission currentEmissions = entryEmissions.get(date);
                float TotalEmissions = (float) currentEmissions.consumptionEmission;
                TotalEmissions+= (float) currentEmissions.transportEmission;
                TotalEmissions+= (float) currentEmissions.foodEmission;

                lineGraphPoints.add(new Entry(index, TotalEmissions));
            }
            index++;
        }

        // Create a LineDataSet with the points
        LineDataSet dataSet = new LineDataSet(lineGraphPoints, "Total Emissions");

        InitLineChartSettings(dataSet, dates);

        // Refresh the chart
        EmissionLineChart.invalidate();
    }

    private void InitBarChartSettings(ArrayList<BarEntry> barEntries){
        BarDataSet barDataSet = new BarDataSet(barEntries, "Emission Breakdown");
        barDataSet.setColors(Color.RED, Color.GREEN, Color.BLUE);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(18f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setTextSize(18f);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setTextSize(18f);

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(18f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(3);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int) value) {
                    case 0:
                        return "Transport";
                    case 1:
                        return "Food";
                    case 2:
                        return "Consumption";
                    default:
                        return "";
                }
            }
        });
    }


    private void updateBarChart() {
        if (entryEmissions.isEmpty()) return;

        float totalTransportEmission = 0;
        float totalFoodEmission = 0;
        float totalConsumptionEmission = 0;

        for (TotalEntryEmission entry : entryEmissions.values()) {
            totalTransportEmission += entry.transportEmission;
            totalFoodEmission += entry.foodEmission;
            totalConsumptionEmission += entry.consumptionEmission;
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, totalTransportEmission));
        barEntries.add(new BarEntry(1, totalFoodEmission));
        barEntries.add(new BarEntry(2, totalConsumptionEmission));

        InitBarChartSettings(barEntries);

        barChart.invalidate();
    }


    /*
        Update TransportEntry Emissions for the given date with the given emission amount
        Also updates the linechart
     */

    private void putTransportEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            entryEmissions.get(date).transportEmission+=emission;
        }else{
            entryEmissions.put(date, new TotalEntryEmission(emission, 0, 0));
        }
        updateBarChart();
        updateLineChart();
    }

    /*
        Update FoodEntry Emissions for the given date with the given emission amount
        Also updates the linechart
     */
    private void putFoodEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            entryEmissions.get(date).foodEmission+=emission;
        }else{
            entryEmissions.put(date, new TotalEntryEmission(0, emission, 0));
        }

        updateBarChart();
        updateLineChart();
    }

    /*
        Update ConsumptionEntry Emissions for the given date with the given emission amount
        Also updates the linechart
     */
    private void putConsumptionEntry(double emission, String date){
        if(entryEmissions.containsKey(date)){
            entryEmissions.get(date).consumptionEmission+=emission;
        }else{
            entryEmissions.put(date, new TotalEntryEmission(0, 0, emission));
        }

        updateBarChart();
        updateLineChart();
    }

    @Override
    public void TransportAction(double AdditionalEmissions, String Date) {
        putTransportEntry(AdditionalEmissions, Date);
        totalEmissions += AdditionalEmissions;
        totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
    }

    @Override
    public void FoodAction(double AdditionalEmissions, String Date) {
        putFoodEntry(AdditionalEmissions, Date);
        totalEmissions += AdditionalEmissions;
        totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
    }

    @Override
    public void ConsumptionAction(double AdditionalEmissions, String Date) {
        putConsumptionEntry(AdditionalEmissions, Date);

        totalEmissions += AdditionalEmissions;
        totalEmissionsView.setText(String.format(Locale.getDefault(), "%.1f kg CO2e", totalEmissions));
    }

    @Override
    public void init() {
        totalEmissions = 0;
        entryEmissions = new HashMap<>();
    }

    /**
     * Given the range (either "This Week", "This Month", "This Year")
     * return the start date of this given range
     * @param range (either "This Week", "This Month", "This Year")
     */
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

    /**
     * Returns the current date as the end date
     */
    private String getEndDateForRange() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Given the country retrieve the yearly, monthly, and weekly emissions and render it for the
     * user to see
     * @param country (Country of interest)
     */
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
