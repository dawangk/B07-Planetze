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
import android.widget.Switch;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import com.example.b07projectfall2024.NavigationBar.EntryInputs.FoodEntryPage;
import com.google.firebase.database.ValueEventListener;

public class ConsumptionEntry extends Fragment {

    private Context currentContext;

    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private String CurrentSelectedDate;
    private String SelectedConsumptionType;

    private HashMap<String, String> SpinnerOptions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumption_entry, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentContext = getContext();

        SpinnerOptions = new HashMap<>();

        //item drop down
        Spinner ConsumptionTypeDropDown = view.findViewById(R.id.ConsumptionEntry_ItemType);
        Spinner ElectronicDropDown = view.findViewById(R.id.ConsumptionEntry_ElectronicType);
        Spinner OtherTypeDropDown = view.findViewById(R.id.ConsumptionEntry_OtherType);
        Spinner UtilityDropDown = view.findViewById(R.id.ConsumptionEntry_BillType);

        String[] ConsumptionTypeItems = {"", "Clothes", "Electronics", "Utility Bill", "Other"};
        String[] ElectronicItems = {"Smartphone", "Laptop", "TV"};
        String[] OtherPurchaseItems = {"Furniture", "Appliances"};
        String[] UtilityBillItems = {"Electricity", "Water", "Gas"};

        SpinnerGeneralInit(ElectronicDropDown, ElectronicItems, "ElectronicType");
        SpinnerGeneralInit(OtherTypeDropDown, OtherPurchaseItems, "OtherPurchaseType");
        SpinnerGeneralInit(UtilityDropDown, UtilityBillItems, "UtilityBillType");

        //Helps map each layout to its respective drop down item value
        HashMap<String, LinearLayout> DynamicFieldsMap = new HashMap<>();

        DynamicFieldsMap.put("Clothes", view.findViewById(R.id.ConsumptionEntry_ClothingFields));
        DynamicFieldsMap.put("Electronics", view.findViewById(R.id.ConsumptionEntry_ElectronicsFields));
        DynamicFieldsMap.put("Utility Bill", view.findViewById(R.id.ConsumptionEntry_UtilityBill));
        DynamicFieldsMap.put("Other", view.findViewById(R.id.ConsumptionEntry_OtherPurchase));

        Button Submit = view.findViewById(R.id.ConsumptionEntry_Submit);

        ConsumptionTypeDropDownInit(ConsumptionTypeDropDown, ConsumptionTypeItems,DynamicFieldsMap, Submit);

        TextView dateTextView = view.findViewById(R.id.ConsumptionEntry_Date);
        DateFieldInit(dateTextView);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadConsumptionEntry(view);
            }
        });

        return view;
    }

    /*
    Uploads the Transport entry under the entry/{Date}/consumption directory to firebase for the current user
    (where Date is the selected date) in the following format:
    4 cases to consider: Transport type is either "Clothes", "Electronics", "Utility Bill", "Other":
    Case "Clothes":
        The information with the following format will be uploaded to firebase:
        {
            BoughtItem: "Clothes",
            EcoFriendly: Boolean,
            NmbClothingBought: Integer
        }
    Case "Electronics":
        The information with the following format will be uploaded to firebase:
        {
            BoughtItem: "Electronics",
            ElectronicType: String,
            NmbPurchased: Integer
        }
    Case "Utility Bill":
        The information with the following format will be uploaded to firebase:
        {
            BoughtItem: "Utility Bill",
            BillPrice: Integer,
            UtilityType: String
        }
    Case "Other":
        The information with the following format will be uploaded to firebase:
        {
            BoughtItem: "Other",
            NmbPurchased: Integer,
            ItemType: String
        }

     */
    private void UploadConsumptionEntry(View view){
        if(SelectedConsumptionType.isEmpty()) return;
        HashMap<String, Object> data = new HashMap<>();

        switch (SelectedConsumptionType){
            case "Clothes":
                EditText nmbClothesField =  view.findViewById(R.id.ConsumptionEntry_NmbClothing);
                if(nmbClothesField.getText().toString().isEmpty()){
                    MissingErrorField(nmbClothesField);
                    return;
                }
                int nmbClothes = Integer.parseInt(nmbClothesField.getText().toString());
                boolean ecoFriendly = ((Switch)view.findViewById(R.id.ConsumptionEntry_EcoFriendly)).isChecked();
                data.put("NmbClothingBought", nmbClothes);
                data.put("EcoFriendly", ecoFriendly);
                break;
            case "Electronics":
                EditText nmbElectronicsField =  view.findViewById(R.id.ConsumptionEntry_NmbElectronics);
                if(nmbElectronicsField.getText().toString().isEmpty()){
                    MissingErrorField(nmbElectronicsField);
                    return;
                }
                int nmbElectronics = Integer.parseInt(nmbElectronicsField.getText().toString());
                data.put("NmbPurchased", nmbElectronics);
                data.put("ElectronicType", SpinnerOptions.get("ElectronicType"));
                break;
            case "Utility Bill":
                EditText BillPriceField =  view.findViewById(R.id.ConsumptionEntry_BillPrice);
                if(BillPriceField.getText().toString().isEmpty()){
                    MissingErrorField(BillPriceField);
                    return;
                }
                int BillPrice = Integer.parseInt(BillPriceField.getText().toString());
                data.put("UtilityType", SpinnerOptions.get("UtilityBillType"));
                data.put("BillPrice",BillPrice);

                //Keeping track of the habit if user is tracking it.
                String utilType = SpinnerOptions.get("UtilityBillType");
                DatabaseReference habitsRef = db.child("users").child(mAuth.getUid()).child("Habits");

                if (utilType.equals("Gas") && BillPrice < 100) {
                    DatabaseReference MinimalGasBillRef = habitsRef.child("MinimalGasBill");
                    trackHabit(MinimalGasBillRef);
                } else if (utilType.equals("Electricity") && BillPrice < 100) {
                    DatabaseReference MinimalElectricityBillRef = habitsRef.child("MinimalElectricityBill");
                    trackHabit(MinimalElectricityBillRef);
                } else if (utilType.equals("Water") && BillPrice < 100) {
                    DatabaseReference MinimalWaterBillRef = habitsRef.child("MinimalWaterBill");
                    trackHabit(MinimalWaterBillRef);
                }
                break;
            case "Other":
                EditText NmbPurchasedField =  view.findViewById(R.id.ConsumptionEntry_NmbOther);
                if(NmbPurchasedField.getText().toString().isEmpty()){
                    MissingErrorField(NmbPurchasedField);
                    return;
                }
                int NmbPurchased = Integer.parseInt(NmbPurchasedField.getText().toString());
                data.put("ItemType", SpinnerOptions.get("OtherPurchaseType"));
                data.put("NmbPurchased", NmbPurchased);
                break;
            default:
                Toast.makeText(currentContext, "Selected Item Key Error", Toast.LENGTH_SHORT).show();
                return;
        }
        data.put("BoughtItem", SelectedConsumptionType);

        DatabaseReference ChildRef = db.child("users").child(mAuth.getUid()).child("entries").child(CurrentSelectedDate).child("consumption").push();
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
        Initializes ItemTypeDropDown
        In its listener, along with setting the Transport type it also helps toggle fields visibility
        depending on the selected transportation type
     */
    private void ConsumptionTypeDropDownInit(Spinner DropDown, String[] DropDownItems, HashMap<String, LinearLayout> DynamicFieldsMap, Button Submit){
        SpinnerItemInit(DropDown, DropDownItems);

        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedConsumptionType = parent.getItemAtPosition(position).toString();

                for(LinearLayout i: DynamicFieldsMap.values()) i.setVisibility(View.GONE);

                Submit.setVisibility(View.GONE);

                if(SelectedConsumptionType.isEmpty()) return;

                if(!DynamicFieldsMap.containsKey(SelectedConsumptionType)){
                    Toast.makeText(currentContext, "Key Value Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Submit.setVisibility(View.VISIBLE);

                DynamicFieldsMap.get(SelectedConsumptionType).setVisibility(View.VISIBLE);
                Toast.makeText(currentContext, "Selected: " + SelectedConsumptionType, Toast.LENGTH_SHORT).show();
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
}
