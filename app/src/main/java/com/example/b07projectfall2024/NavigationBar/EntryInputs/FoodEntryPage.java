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
import android.widget.Spinner;
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

public class FoodEntryPage extends Fragment {

    private Context currentContext;

    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private String CurrentSelectedDate;
    private String SelectedMeal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_entry, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentContext = getContext();

        //item drop down
        Spinner MealType = view.findViewById(R.id.FoodEntry_MealType);

        String[] MealTypeItems = {"", "Vegetarian", "Pork", "Beef", "Fish", "Chicken"};

        SelectedMealSpinnerInit(MealType, MealTypeItems);

        TextView dateTextView = view.findViewById(R.id.FoodEntry_Date);
        DateFieldInit(dateTextView);

        Button Submit = view.findViewById(R.id.FoodEntry_Submit);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFoodEntry(view);
            }
        });

        return view;
    }

    /*
    Uploads the Transport entry under the entry/{Date}/food directory to firebase for the current user
    (where Date is the selected date) in the following format:
        {
            MealType: String,
            ThrownFood: Integer
        }
     */
    private void UploadFoodEntry(View view){
        HashMap<String, Object> data = new HashMap<>();

        EditText NmbConsumedServingsField =  view.findViewById(R.id.FoodEntry_NmbConsumedServings);
        if(NmbConsumedServingsField.getText().toString().isEmpty()){
            MissingErrorField(NmbConsumedServingsField);
            return;
        }

        data.put("NmbConsumedServings", Integer.parseInt(NmbConsumedServingsField.getText().toString()));

        data.put("MealType", SelectedMeal);

        DatabaseReference ChildRef = db.child("users").child(mAuth.getUid()).child("entries").child(CurrentSelectedDate).child("food").push();
        ChildRef.setValue(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(currentContext, "Successfully stored entry", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(currentContext, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //Initializes the spinner, MealType
    private void SelectedMealSpinnerInit(Spinner DropDown, String[] DropDownItems){
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
                SelectedMeal = parent.getItemAtPosition(position).toString();
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

}
