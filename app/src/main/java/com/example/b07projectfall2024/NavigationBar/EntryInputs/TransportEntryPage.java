package com.example.b07projectfall2024.NavigationBar.EntryInputs;

import android.app.DatePickerDialog;
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
import java.util.List;
import java.util.Locale;

public class TransportEntryPage extends Fragment {

    private HomeActivity home;

    public TransportEntryPage(HomeActivity home){
        this.home = home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport_entry, container, false);

        Spinner dropdownMenu = view.findViewById(R.id.TransportEntry_TransportType);
        LinearLayout CarContainer = view.findViewById(R.id.TransportEntry_CarOption);
        LinearLayout PublicContainer = view.findViewById(R.id.TransportEntry_PublicOption);
        LinearLayout PlaneContainer = view.findViewById(R.id.TransportEntry_FlightOption);
        Button Submit = view.findViewById(R.id.TransportEntry_SubmitButton);

        String[] dropdownItems = {"", "Public Transport", "Car", "Plane"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                home,
                android.R.layout.simple_spinner_item,
                dropdownItems
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdownMenu.setAdapter(adapter);

        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                CarContainer.setVisibility(View.GONE);
                PublicContainer.setVisibility(View.GONE);
                PlaneContainer.setVisibility(View.GONE);
                Submit.setVisibility(View.GONE);

                if(selectedItem.isEmpty()) return;

                Submit.setVisibility(View.VISIBLE);

                switch (selectedItem){
                    case "Public Transport":
                        PublicContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Car":
                        CarContainer.setVisibility(View.VISIBLE);
                        break;
                    case "Plane":
                        PlaneContainer.setVisibility(View.VISIBLE);
                        break;
                }

                Toast.makeText(home, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        TextView dateTextView = view.findViewById(R.id.TransportEntry_Date);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        dateTextView.setText(todayDate);

        dateTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    home,
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

        return view;
    }
}
