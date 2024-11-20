package com.example.b07projectfall2024.NavigationBar.EntryInputs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07projectfall2024.HomeActivity;
import com.example.b07projectfall2024.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TransportEntryPage extends Fragment {

    private Context currentContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport_entry, container, false);

        currentContext = getContext();

        LinearLayout CarContainer = view.findViewById(R.id.TransportEntry_CarOption);
        LinearLayout PublicContainer = view.findViewById(R.id.TransportEntry_PublicOption);
        LinearLayout PlaneContainer = view.findViewById(R.id.TransportEntry_FlightOption);

        Button Submit = view.findViewById(R.id.TransportEntry_SubmitButton);

        Spinner TransportDropDown = view.findViewById(R.id.TransportEntry_TransportType);
        Spinner DistanceUnitDropDown = view.findViewById(R.id.TrasportEntry_DistanceUnit);
        Spinner CarTypeDropDown = view.findViewById(R.id.TransportEntry_CarType);

        String[] TransportTypeDropDownItems = {"", "Public Transport", "Car", "Plane"};
        String[] DistanceUnitDropDownItems = {"Kilometers", "Miles"};
        String[] CarTypeDropDownItems = {"Gasoline", "Diesel", "Hybrid", "Electric"};

        SpinnerInit(DistanceUnitDropDown, DistanceUnitDropDownItems);
        SpinnerInit(CarTypeDropDown, CarTypeDropDownItems);

        HashMap<String, LinearLayout> DynamicFieldsMap = new HashMap<String, LinearLayout>();

        DynamicFieldsMap.put("Public Transport", PublicContainer);
        DynamicFieldsMap.put("Car", CarContainer);
        DynamicFieldsMap.put("Plane", PlaneContainer);

        TransportTypeInit(TransportDropDown, TransportTypeDropDownItems, DynamicFieldsMap, Submit);

        TextView dateTextView = view.findViewById(R.id.TransportEntry_Date);
        DateFieldInit(dateTextView);

        return view;
    }

    private void SpinnerInit(Spinner DropDown, String[] DropDownItems){
        ArrayAdapter<String> TransportTypeAdapter = new ArrayAdapter<>(
                currentContext,
                android.R.layout.simple_spinner_item,
                DropDownItems
        );

        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DropDown.setAdapter(TransportTypeAdapter);
    }

    private void TransportTypeInit(Spinner TransportDropDown, String[] TransportTypeDropDownItems, HashMap<String, LinearLayout> DynamicFieldsMap, Button Submit){
        SpinnerInit(TransportDropDown, TransportTypeDropDownItems);

        TransportDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                for(LinearLayout i: DynamicFieldsMap.values()) i.setVisibility(View.GONE);

                Submit.setVisibility(View.GONE);

                if(selectedItem.isEmpty()) return;
                if(!DynamicFieldsMap.containsKey(selectedItem)){
                    Toast.makeText(currentContext, "Key Value Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Submit.setVisibility(View.VISIBLE);
                DynamicFieldsMap.get(selectedItem).setVisibility(View.VISIBLE);
                Toast.makeText(currentContext, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private void DateFieldInit(TextView dateTextView){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        dateTextView.setText(todayDate);

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
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

}
