package com.example.b07projectfall2024.NavigationBar.EntryInputs;

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

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class FoodEntryPage extends Entry {

    private String SelectedMeal;


    public FoodEntryPage(){
        super();
    }
    public FoodEntryPage(String date){
        super(date);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_food_entry, container, false);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        currentContext = getContext();

        //item drop down
        Spinner MealType = view.findViewById(R.id.FoodEntry_MealType);

        String[] MealTypeItems = {"Vegetarian", "Pork", "Beef", "Fish", "Chicken"};

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

        //Keeping track of the habit if user is tracking it.
        if (SelectedMeal.equals("Vegetarian")) {
            DatabaseReference eatingVeggiesHabit = db.getRef().child("users").child(mAuth.getUid()).child("Habits").child("Eating Vegetarian");
            trackHabit(eatingVeggiesHabit);
        } else if (SelectedMeal.equals("Fish")) {
            DatabaseReference eatingFishHabit = db.getRef().child("users").child(mAuth.getUid()).child("Habits").child("Eating Fish");
            trackHabit(eatingFishHabit);
        }
        //Keeping track of the anti-habit if user is tracking the habit
        else if (SelectedMeal.equals("Chicken")) {
            DatabaseReference eatingFishHabit = db.getRef().child("users").child(mAuth.getUid()).child("Habits").child("Eating Fish");
            trackAntiHabit(eatingFishHabit, "Eating Fish");
        } else {
            DatabaseReference eatingVeggiesHabit = db.getRef().child("users").child(mAuth.getUid()).child("Habits").child("Eating Vegetarian");
            trackAntiHabit(eatingVeggiesHabit, "Eating Vegetarian");
        }


        DatabaseReference ChildRef = db.child("users").child(mAuth.getUid()).child("entries").child(CurrentSelectedDate).child("food").push();
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
}
