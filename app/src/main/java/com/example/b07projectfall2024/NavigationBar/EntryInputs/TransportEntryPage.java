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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07projectfall2024.HomeActivity;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import com.example.b07projectfall2024.NavigationBar.EntryInputs.FoodEntryPage;

public class TransportEntryPage extends Fragment {
    private Context currentContext;

    private HashMap<String, String> SpinnerOptions;

    private String TransportType;

    private DatabaseReference db;
    private FirebaseAuth mAuth;

    private String CurrentSelectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport_entry, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        SpinnerOptions = new HashMap<>();

        currentContext = getContext();

        //default hidden layouts (will become visible depending on selected transportation type)
        LinearLayout CarContainer = view.findViewById(R.id.TransportEntry_CarOption);
        LinearLayout PublicContainer = view.findViewById(R.id.TransportEntry_PublicOption);
        LinearLayout PlaneContainer = view.findViewById(R.id.TransportEntry_FlightOption);
        LinearLayout WalkedCycledContainer = view.findViewById(R.id.TransportEntry_WalkCycled);

        Button Submit = view.findViewById(R.id.TransportEntry_SubmitButton);

        //item drop downs
        Spinner TransportDropDown = view.findViewById(R.id.TransportEntry_TransportType);
        Spinner CarDistanceUnitDropDown = view.findViewById(R.id.TrasportEntry_DistanceUnit);
        Spinner CarTypeDropDown = view.findViewById(R.id.TransportEntry_CarType);
        Spinner PublicTransportTypeDropDown = view.findViewById(R.id.TransportEntry_PublicType);
        Spinner FlightTypeDropDown = view.findViewById(R.id.TransportEntry_FlightType);
        Spinner WalkCycledDistanceUnitDropDown = view.findViewById(R.id.TransportEntry_WalkedCycledDistanceUnit);

        String[] TransportTypeDropDownItems = {"", "Public Transport", "Car", "Plane", "Walked", "Cycled"};
        String[] DistanceUnitDropDownItems = {"Kilometers", "Miles"};
        String[] CarTypeDropDownItems = {"Gasoline", "Diesel", "Hybrid", "Electric"};
        String[] PublicTransportTypeDropDownItems = {"Bus", "Train", "Subway"};
        String[] FlightTypeDropDownItems = {"Short-haul (<1,500 km)", "Long-haul (>1,500 km)"};

        //Helps map each layout to its respective drop down item value
        HashMap<String, LinearLayout> DynamicFieldsMap = new HashMap<String, LinearLayout>();

        DynamicFieldsMap.put("Public Transport", PublicContainer);
        DynamicFieldsMap.put("Car", CarContainer);
        DynamicFieldsMap.put("Plane", PlaneContainer);
        DynamicFieldsMap.put("Walked", WalkedCycledContainer);
        DynamicFieldsMap.put("Cycled", WalkedCycledContainer);

        SpinnerGeneralInit(CarDistanceUnitDropDown, DistanceUnitDropDownItems, "CarDistanceUnit");
        SpinnerGeneralInit(WalkCycledDistanceUnitDropDown, DistanceUnitDropDownItems, "WalkedCycledDistanceUnit");
        SpinnerGeneralInit(CarTypeDropDown, CarTypeDropDownItems, "CarType");
        SpinnerGeneralInit(PublicTransportTypeDropDown, PublicTransportTypeDropDownItems, "PublicType");
        SpinnerGeneralInit(FlightTypeDropDown, FlightTypeDropDownItems, "FlightType");


        TransportTypeInit(TransportDropDown, TransportTypeDropDownItems, DynamicFieldsMap, Submit);

        TextView dateTextView = view.findViewById(R.id.TransportEntry_Date);
        DateFieldInit(dateTextView);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadTranportEntry(view);
            }
        });

        return view;
    }

    /*
    Uploads the Transport entry under the entry/{Date}/transportation directory to firebase for the current user
    (where Date is the selected date) in the following format:
    3 cases to consider: Transport type is "Public Transport", "Car", "Plane", or "WalkedCycled":
    Case "Public Transport":
        The information with the following format will be uploaded to firebase:
        {
            TransportationType: "Public",
            PublicType: String,
            TimeOnPublic: Integer
        }
    Case "Car":
        The information with the following format will be uploaded to firebase:
        {
            TransportationType: "Car",
            Distance: Integer,
            DistanceUnit: String,
            CarType: String
        }
    Case "Plane":
        The information with the following format will be uploaded to firebase:
        {
            TransportationType: "Plane",
            NmbFlights: Integer
        }
    Case "Walked":
        The information with the following format will be uploaded to firebase:
        {
            TransportationType: "Walked",
            Distance: Integer,
            DistanceUnit: String
        }
    Case "Cycled":
        The information with the following format will be uploaded to firebase:
        {
            TransportationType: "Cycled",
            Distance: Integer,
            DistanceUnit: String
        }
     */
    private void UploadTranportEntry(View view){
        if(TransportType.isEmpty()) return;

        HashMap<String, Object> data = new HashMap<>();
        DatabaseReference transitHabit;

        switch (TransportType){
            case "Public Transport":
                EditText TimeonPublicField = view.findViewById(R.id.TransportEntry_PublicTime);
                if(TimeonPublicField.getText().toString().isEmpty()){
                    MissingErrorField(TimeonPublicField);
                    return;
                }
                int TimeOnPublic = Integer.parseInt(TimeonPublicField.getText().toString());
                data.put("TransportationType", "Public");
                data.put("PublicType", SpinnerOptions.get("PublicType"));
                data.put("TimeOnPublic", TimeOnPublic);

                //Keeping track of the habit if user is tracking it.
                transitHabit = db.child("users").child(mAuth.getUid()).child("Habits").child("Taking the Transit");
                trackHabit(transitHabit);

                break;
            case "Car":
                EditText DistanceDrivenField =  view.findViewById(R.id.TransportEntry_DistanceDriven);
                if(DistanceDrivenField.getText().toString().isEmpty()){
                    MissingErrorField(DistanceDrivenField);
                    return;
                }
                int DistanceDriven = Integer.parseInt(DistanceDrivenField.getText().toString());

                data.put("TransportationType", "Car");
                if(SpinnerOptions.get("CarDistanceUnit").equals("Miles")){
                    data.put("Distance", DistanceDriven);
                    data.put("DistanceUnit", "Miles");
                }else{
                    data.put("Distance", DistanceDriven);
                    data.put("DistanceUnit", "KM");
                }
                data.put("CarType", SpinnerOptions.get("CarType"));

                //Keeping track of the anti-habit of Walking, Biking and Transit if user is tracking any one of them

                transitHabit = db.child("users").child(mAuth.getUid()).child("Habits").child("Walking");
                DatabaseReference transitHabit2 = db.child("users").child(mAuth.getUid()).child("Habits").child("Biking");
                DatabaseReference transitHabit3 = db.child("users").child(mAuth.getUid()).child("Habits").child("Taking the Transit");
                if (!trackAntiHabit(transitHabit, "Walking") && !trackAntiHabit(transitHabit2, "Biking")) {
                    trackAntiHabit(transitHabit3, "Taking the Transit");
                }

                break;
            case "Plane":
                EditText NmbFlightsField =  view.findViewById(R.id.TransportEntry_NmbFlights);
                if(NmbFlightsField.getText().toString().isEmpty()){
                    MissingErrorField(NmbFlightsField);
                    return;
                }
                int NmbFlights = Integer.parseInt(NmbFlightsField.getText().toString());

                data.put("TransportationType", "Plane");
                data.put("NmbFlights", NmbFlights);
                data.put("FlightType", SpinnerOptions.get("FlightType"));
                break;
            case "Walked":
                EditText DistanceWalkedCycledField =  view.findViewById(R.id.TransportEntry_DistanceWalkedCycled);
                if(DistanceWalkedCycledField.getText().toString().isEmpty()){
                    MissingErrorField(DistanceWalkedCycledField);
                    return;
                }
                int DistanceWalkedCycled = Integer.parseInt(DistanceWalkedCycledField.getText().toString());
                data.put("TransportationType", "Walked");
                if(SpinnerOptions.get("WalkedCycledDistanceUnit").equals("Miles")){
                    data.put("Distance", DistanceWalkedCycled);
                    data.put("DistanceUnit", "Miles");
                }else{
                    data.put("Distance", DistanceWalkedCycled);
                    data.put("DistanceUnit", "KM");
                }

                //Keeping track of the habit if user is tracking it.
                transitHabit = db.child("users").child(mAuth.getUid()).child("Habits").child("Walking");
                trackHabit(transitHabit);
                break;
            case "Cycled":
                EditText DistanceWalkedCycledField1 =  view.findViewById(R.id.TransportEntry_DistanceWalkedCycled);
                if(DistanceWalkedCycledField1.getText().toString().isEmpty()){
                    MissingErrorField(DistanceWalkedCycledField1);
                    return;
                }
                int DistanceWalkedCycled1 = Integer.parseInt(DistanceWalkedCycledField1.getText().toString());
                data.put("TransportationType", "Cycled");
                if(SpinnerOptions.get("WalkedCycledDistanceUnit").equals("Miles")){
                    data.put("Distance", DistanceWalkedCycled1);
                    data.put("DistanceUnit", "Miles");
                }else{
                    data.put("Distance", DistanceWalkedCycled1);
                    data.put("DistanceUnit", "KM");
                }

                //Keeping track of the habit if user is tracking it.
                transitHabit = db.child("users").child(mAuth.getUid()).child("Habits").child("Biking");
                trackHabit(transitHabit);
                break;
            default:
                Toast.makeText(currentContext, "Key error on upload", Toast.LENGTH_SHORT).show();
                return;
        }

        DatabaseReference ChildRef = db.child("users").child(mAuth.getUid()).child("entries").child(CurrentSelectedDate).child("transportation").push();
        ChildRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(currentContext, "Successfully stored entry", Toast.LENGTH_SHORT).show();
                        popFragment();
                    } else {
                        Toast.makeText(currentContext, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void popFragment(){
        getActivity().getSupportFragmentManager().popBackStack();
    }

    //Assigns the given DropDownItems to the Spinner object, DropDown
    private void SpinnerItemInit(Spinner DropDown, String[] DropDownItems){
        ArrayAdapter<String> TransportTypeAdapter = new ArrayAdapter<>(
                currentContext,
                android.R.layout.simple_spinner_item,
                DropDownItems
        );

        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DropDown.setAdapter(TransportTypeAdapter);
    }

    //Initializes general spinner
    private void SpinnerGeneralInit(Spinner DistanceUnitDropDown, String[] DistanceUnitDropDownItems, String type){
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

    /*
        Initializes TransportType
        In its listener, along with setting the Transport type it also helps toggle fields visibility
        depending on the selected transportation type
     */
    private void TransportTypeInit(Spinner TransportDropDown, String[] TransportTypeDropDownItems, HashMap<String, LinearLayout> DynamicFieldsMap, Button Submit){
        SpinnerItemInit(TransportDropDown, TransportTypeDropDownItems);

        TransportDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                TransportType = selectedItem;

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


    //Initializes DateField allowing users to select any date
    private void DateFieldInit(TextView dateTextView){
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

    private void MissingErrorField(EditText Field){
        SetErrorField(Field, "Missing, Please fill");
    }

    private void SetErrorField(EditText Field, String ErrorMsg){
        Field.setError(ErrorMsg);
        Field.requestFocus();
    }

    private void trackHabit(DatabaseReference habitRef) {
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If habit is being tracked, we check if it's been logged today
                if (snapshot.exists()) {
                    DatabaseReference dayRef = habitRef.child(CurrentSelectedDate);
                    dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //If so, we increment the number of occurrences for the day by 1.
                            if (snapshot.exists()) {
                                dayRef.setValue(snapshot.getValue(Integer.class) + 1);
                            }
                            //Else, we create a new log for today with a value of 1 occurrences.
                            else {
                                HashMap<String, Object> data = new HashMap<String, Object>();
                                data.put(CurrentSelectedDate, 1);
                                habitRef.updateChildren(data);
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

    private boolean trackAntiHabit(DatabaseReference habitRef, String habit) {
        boolean[] status = {false};
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If user is tracking the habit, make a new branch for the anti-habit
                if (snapshot.exists()) {
                    DatabaseReference antiHabitRef = db.child("Habits").child(habit).child("AntiHabit");
                    antiHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String antiHabit = snapshot.getValue(String.class);

                            //Track this occurrence of the anti-habit in the database
                            DatabaseReference userAntiHabitRef = db.child("users").child(mAuth.getUid()).child("AntiHabits").child(antiHabit);
                            DatabaseReference dayRef = userAntiHabitRef.child(CurrentSelectedDate);
                            dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    //If the anti-habit has been tracked today, we increment the number of occurrences for the day by 1
                                    if (snapshot.exists()) {
                                        dayRef.setValue(snapshot.getValue(Integer.class) + 1);
                                        status[0] = true;
                                    }
                                    //Else, we create a new log for today with a value of 1 occurrences.
                                    else {
                                        HashMap<String, Object> data = new HashMap<String, Object>();
                                        data.put(CurrentSelectedDate, 1);
                                        userAntiHabitRef.updateChildren(data);
                                        status[0] = true;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
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

        return status[0];
    }

}
