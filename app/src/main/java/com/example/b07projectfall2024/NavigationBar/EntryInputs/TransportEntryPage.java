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
import com.example.b07projectfall2024.RegisterPage.RegisterActivityView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TransportEntryPage extends Fragment {

    private Context currentContext;
    private String CarDistanceUnit;
    private String CarType;

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

        currentContext = getContext();

        //default hidden layouts (will become visible depending on selected transportation type)
        LinearLayout CarContainer = view.findViewById(R.id.TransportEntry_CarOption);
        LinearLayout PublicContainer = view.findViewById(R.id.TransportEntry_PublicOption);
        LinearLayout PlaneContainer = view.findViewById(R.id.TransportEntry_FlightOption);

        Button Submit = view.findViewById(R.id.TransportEntry_SubmitButton);

        //item drop downs
        Spinner TransportDropDown = view.findViewById(R.id.TransportEntry_TransportType);
        Spinner DistanceUnitDropDown = view.findViewById(R.id.TrasportEntry_DistanceUnit);
        Spinner CarTypeDropDown = view.findViewById(R.id.TransportEntry_CarType);

        String[] TransportTypeDropDownItems = {"", "Public Transport", "Car", "Plane"};
        String[] DistanceUnitDropDownItems = {"Kilometers", "Miles"};
        String[] CarTypeDropDownItems = {"Gasoline", "Diesel", "Hybrid", "Electric"};

        //Helps map each layout to its respective drop down item value
        HashMap<String, LinearLayout> DynamicFieldsMap = new HashMap<String, LinearLayout>();

        DynamicFieldsMap.put("Public Transport", PublicContainer);
        DynamicFieldsMap.put("Car", CarContainer);
        DynamicFieldsMap.put("Plane", PlaneContainer);

        DistanceUnitDropDownInit(DistanceUnitDropDown, DistanceUnitDropDownItems);
        CarTypeDropDownInit(CarTypeDropDown, CarTypeDropDownItems);
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
    Uploads the Transport entry under the entry/{Date} directory to firebase for the current user
    (where Date is the selected date) in the following format:
    3 cases to consider: Transport type is "Public Transport", "Car", or "Plane":
    Case "Public Transport":
        The information with the following format will be uploaded to firebase:
        {
            type: "transportation",
            TransportationType: "Public",
            TimeOnPublic: Integer
        }
    Case "Car":
        The information with the following format will be uploaded to firebase:
        {
            type: "transportation",
            TransportationType: "Car",
            Distance: Integer,
            CarType: String
        }
    Case "Plane":
        The information with the following format will be uploaded to firebase:
        {
            type: "transportation",
            TransportationType: "Plane",
            FlightTime: Integer
        }
     */
    private void UploadTranportEntry(View view){
        if(TransportType.isEmpty()) return;

        HashMap<String, Object> data = new HashMap<>();

        switch (TransportType){
            case "Public Transport":
                int TimeOnPublic = Integer.parseInt(((EditText) view.findViewById(R.id.TransportEntry_PublicTime)).getText().toString());
                data.put("TransportationType", "Public");
                data.put("TimeOnPublic", TimeOnPublic);
                break;
            case "Car":
                int DistanceDriven = Integer.parseInt(((EditText) view.findViewById(R.id.TransportEntry_DistanceDriven)).getText().toString());
                data.put("TransportationType", "Car");
                if(CarDistanceUnit.equals("Miles")){
                    data.put("Distance", (int)(DistanceDriven*1.60934));
                }else{
                    data.put("Distance", DistanceDriven);
                }
                data.put("CarType", CarType);
                break;
            case "Plane":
                int FlightTime = Integer.parseInt(((EditText) view.findViewById(R.id.TransportEntry_FlightTime)).getText().toString());
                data.put("TransportationType", "Plane");
                data.put("FlightTime", FlightTime);
                break;
            default:
                Toast.makeText(currentContext, "Key error on upload", Toast.LENGTH_SHORT).show();
                return;
        }

        data.put("type", "transportation");

        DatabaseReference ChildRef = db.child("users").child(mAuth.getUid()).child("entries").child(CurrentSelectedDate).push();
        ChildRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(currentContext, "Successfully stored entry", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(currentContext, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Assigns the given DropDownItems to the Spinner object, DropDown
    private void SpinnerInit(Spinner DropDown, String[] DropDownItems){
        ArrayAdapter<String> TransportTypeAdapter = new ArrayAdapter<>(
                currentContext,
                android.R.layout.simple_spinner_item,
                DropDownItems
        );

        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DropDown.setAdapter(TransportTypeAdapter);
    }

    //Initializes DistanceUnitDropDown
    private void DistanceUnitDropDownInit(Spinner DistanceUnitDropDown, String[] DistanceUnitDropDownItems){
        SpinnerInit(DistanceUnitDropDown, DistanceUnitDropDownItems);

        DistanceUnitDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CarDistanceUnit = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //Initializes CarTypeDropDown
    private void CarTypeDropDownInit(Spinner CarTypeDropDown, String[] CarTypeDropDownItems){
        SpinnerInit(CarTypeDropDown, CarTypeDropDownItems);

        CarTypeDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CarType = parent.getItemAtPosition(position).toString();
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
        SpinnerInit(TransportDropDown, TransportTypeDropDownItems);

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

}
