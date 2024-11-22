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

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ConsumptionEntry extends Fragment {

    private Context currentContext;

    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private String CurrentSelectedDate;
    private String SelectedItemType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumption_entry, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentContext = getContext();

        //item drop down
        Spinner ItemTypeDropDown = view.findViewById(R.id.ConsumptionEntry_ItemType);

        String[] ItemTypeItems = {"", "Clothes", "Electronics"};

        //Helps map each layout to its respective drop down item value
        HashMap<String, LinearLayout> DynamicFieldsMap = new HashMap<>();

        DynamicFieldsMap.put("Clothes", view.findViewById(R.id.ConsumptionEntry_ClothingFields));
        DynamicFieldsMap.put("Electronics", view.findViewById(R.id.ConsumptionEntry_ElectronicsFields));

        Button Submit = view.findViewById(R.id.ConsumptionEntry_Submit);

        ItemTypeDropDownInit(ItemTypeDropDown, ItemTypeItems,DynamicFieldsMap, Submit);

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
    Uploads the Transport entry under the entry/{Date} directory to firebase for the current user
    (where Date is the selected date) in the following format:
    2 cases to consider: Transport type is either "Clothes" or "Electronics":
    Case "Clothes":
        The information with the following format will be uploaded to firebase:
        {
            type: "consumption",
            BoughtItem: "Clothes",
            EcoFriendly: Boolean,
            NmbClothingBought: Integer
        }
    Case "Electronics":
        The information with the following format will be uploaded to firebase:
        {
            type: "consumption",
            BoughtItem: "Electronics",
            NmbLargeElectronics: Integer
        }
     */
    private void UploadConsumptionEntry(View view){
        if(SelectedItemType.isEmpty()) return;
        HashMap<String, Object> data = new HashMap<>();

        switch (SelectedItemType){
            case "Clothes":
                int nmbClothes = Integer.parseInt(((EditText) view.findViewById(R.id.ConsumptionEntry_NmbClothing)).getText().toString());
                boolean ecoFriendly = ((Switch)view.findViewById(R.id.ConsumptionEntry_EcoFriendly)).isChecked();
                data.put("NmbClothingBought", nmbClothes);
                data.put("EcoFriendly", ecoFriendly);
                break;
            case "Electronics":
                int nmbElectronics = Integer.parseInt(((EditText) view.findViewById(R.id.ConsumptionEntry_NmbElectronics)).getText().toString());
                data.put("NmbLargeElectronics", nmbElectronics);
                break;
            default:
                Toast.makeText(currentContext, "Selected Item Key Error", Toast.LENGTH_SHORT).show();
                return;
        }

        data.put("type", "consumption");
        data.put("BoughtItem", SelectedItemType);

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

    /*
        Initializes ItemTypeDropDown
        In its listener, along with setting the Transport type it also helps toggle fields visibility
        depending on the selected transportation type
     */
    private void ItemTypeDropDownInit(Spinner DropDown, String[] DropDownItems, HashMap<String, LinearLayout> DynamicFieldsMap, Button Submit){
        ArrayAdapter<String> TransportTypeAdapter = new ArrayAdapter<>(
                currentContext,
                android.R.layout.simple_spinner_item,
                DropDownItems
        );

        TransportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DropDown.setAdapter(TransportTypeAdapter);

        DropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedItemType = parent.getItemAtPosition(position).toString();

                for(LinearLayout i: DynamicFieldsMap.values()) i.setVisibility(View.GONE);

                Submit.setVisibility(View.GONE);

                if(SelectedItemType.isEmpty()) return;

                if(!DynamicFieldsMap.containsKey(SelectedItemType)){
                    Toast.makeText(currentContext, "Key Value Error", Toast.LENGTH_SHORT).show();
                    return;
                }
                Submit.setVisibility(View.VISIBLE);

                DynamicFieldsMap.get(SelectedItemType).setVisibility(View.VISIBLE);
                Toast.makeText(currentContext, "Selected: " + SelectedItemType, Toast.LENGTH_SHORT).show();
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
