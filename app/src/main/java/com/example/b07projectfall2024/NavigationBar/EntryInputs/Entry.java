package com.example.b07projectfall2024.NavigationBar.EntryInputs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
public abstract class Entry extends Fragment{
    protected Context currentContext;

    protected DatabaseReference db;
    protected FirebaseAuth mAuth;
    protected String CurrentSelectedDate;


    protected HashMap<String, String> SpinnerOptions;

    public Entry(){
        CurrentSelectedDate = "";
    }

    public Entry(String date){
        CurrentSelectedDate = date;
    }

    @Nullable
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);


    protected void popFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    //Assigns the given DropDownItems to the Spinner object, DropDown
    protected void SpinnerItemInit(Spinner DropDown, String[] DropDownItems){
        ArrayAdapter<String> TransportTypeAdapter = new ArrayAdapter<>(
                currentContext,
                android.R.layout.simple_spinner_item,
                DropDownItems
        );

        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DropDown.setAdapter(TransportTypeAdapter);
    }

    //Initializes general spinner
    protected void SpinnerGeneralInit(Spinner DistanceUnitDropDown, String[] DistanceUnitDropDownItems, String type){
        SpinnerItemInit(DistanceUnitDropDown, DistanceUnitDropDownItems);

        DistanceUnitDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerOptions.put(type, parent.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }



    //Initializes DateField allowing users to select any date
    protected void DateFieldInit(TextView dateTextView){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());


        if(CurrentSelectedDate.isEmpty()) {
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
        }else{
            Log.d("Edit", "Static date edit" + CurrentSelectedDate);
        }
        dateTextView.setText(CurrentSelectedDate);

    }

    protected void MissingErrorField(EditText Field){
        SetErrorField(Field, "Missing, Please fill");
    }

    protected void SetErrorField(EditText Field, String ErrorMsg){
        Field.setError(ErrorMsg);
        Field.requestFocus();
    }
}