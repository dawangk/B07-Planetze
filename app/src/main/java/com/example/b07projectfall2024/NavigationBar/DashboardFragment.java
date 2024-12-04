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
public class DashboardFragment extends Fragment implements DailyEmissionGetterReceiver {

    Context currentContext;
    String CurrentSelectedDate;

    double totalEmissions;
    double transportEmissions;
    double dietEmissions;
    double consumptionEmissions;

    TextView total_emissions;
    TextView transport_emissions;
    TextView diet_emissions;
    TextView consumption_emissions;

    DailyEmissionsServer EmissionsServer;

    public DashboardFragment(){
        EmissionsServer = new DailyEmissionsServer(this);
    }

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
        EmissionsServer.UpdateData(CurrentSelectedDate);

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
                EmissionsServer.UpdateData(CurrentSelectedDate);
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
     * Rounds a decimal value to 3 decimal places
     * @param toRound The 'double' value to round
     * @return toRound rounded to 3 decimal places as a 'double'
     */
    private double roundThreeDec(double toRound) {
        return (double)Math.round(toRound * 1000) / 1000;
    }

    @Override
    public void TransportAction(double AdditionalEmissions, String Date) {
        transportEmissions = roundThreeDec(transportEmissions + AdditionalEmissions);
        totalEmissions = roundThreeDec(totalEmissions + AdditionalEmissions);
        transport_emissions.setText("Transportation Emissions: " + transportEmissions + "kg");
        total_emissions.setText(totalEmissions + "");
    }

    @Override
    public void FoodAction(double AdditionalEmissions, String Date) {
        dietEmissions = roundThreeDec(dietEmissions + AdditionalEmissions);
        totalEmissions = roundThreeDec(totalEmissions + AdditionalEmissions);
        diet_emissions.setText("Diet Emissions: " + dietEmissions + "kg");
        total_emissions.setText(totalEmissions + "");
    }

    @Override
    public void ConsumptionAction(double AdditionalEmissions, String Date) {
        consumptionEmissions = roundThreeDec(consumptionEmissions + AdditionalEmissions);
        totalEmissions = roundThreeDec(totalEmissions + AdditionalEmissions);
        consumption_emissions.setText("Consumption Emissions: " + consumptionEmissions + "kg");
        total_emissions.setText(totalEmissions + "");
    }

    @Override
    public void init() {
        //Initializing emission values to zero
        totalEmissions = 0;
        transportEmissions = 0;
        dietEmissions = 0;
        consumptionEmissions = 0;

        total_emissions.setText(totalEmissions + "");
        transport_emissions.setText("Transportation Emissions: 0kg");
        diet_emissions.setText("Diet Emissions: 0kg");
        consumption_emissions.setText("Consumptions Emissions: 0kg");
    }
}

