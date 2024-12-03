package com.example.b07projectfall2024.HabitTracking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SuggestHabitFragment extends Fragment {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public SuggestHabitFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggest_habit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        TextView suggestedHabit = rootView.findViewById(R.id.suggestedHabit);
        TextView suggestedHabitDesc = rootView.findViewById(R.id.suggestedHabitDesc);
        TextView habitTextDisplay = rootView.findViewById(R.id.habitTextDisplay);
        TextView habitTotal = rootView.findViewById(R.id.habitTotal);

        DatabaseReference entriesRef = ref.child("users").child(user.getUid()).child("entries");
        int[] walkingCount = {0};
        int[] bikingCount = {0};
        int[] transitCount = {0};
        int[] minWaterBillCount = {0};
        int[] minGasBillCount = {0};
        int[] minElectricityBillCount = {0};
        int[] eatingVegCount = {0};
        int[] eatingFishCount = {0};
        int[] max = {0};
        int[][] counts = {walkingCount, bikingCount, transitCount, minWaterBillCount,
                minGasBillCount, minElectricityBillCount, eatingFishCount, eatingVegCount};
        String[] habitNames = {"Walking", "Biking", "Taking the Transit", "Minimal Water Bill",
                "Minimal Gas Bill", "Minimal Electricity Bill", "Eating Fish", "Eating Vegetarian"};

        //Only considering habits that are not currently being tracked

        String[] habitToSuggest = {""};
        
        entriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot date : snapshot.getChildren()) {
                    for (DataSnapshot type : date.getChildren()) {
                        String entryType = type.getKey();
                        for (DataSnapshot entry : type.getChildren()) {
                            switch (entryType) {
                                case "consumption":
                                    String consumptionType = entry.child("BoughtItem").getValue(String.class);
                                    if (consumptionType.equals("Utility Bill")) {
                                        String utilityType = entry.child("UtilityType").getValue(String.class);
                                        double billPrice = entry.child("BillPrice").getValue(Double.class);
                                        //If bill price is less than 100, check bill type and add 1 to count
                                        if (billPrice < 100) {
                                            switch (utilityType) {
                                                case "Water":
                                                    minWaterBillCount[0]++;
                                                    break;
                                                case "Gas":
                                                    minGasBillCount[0]++;
                                                    break;
                                                case "Electricity":
                                                    minElectricityBillCount[0]++;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                    break;

                                case "transportation":

                                    String transportType = entry.child("TransportationType").getValue(String.class);
                                    switch (transportType) {
                                        case "Walked":
                                            walkingCount[0]++;
                                            break;
                                        case "Cycled":
                                            bikingCount[0]++;
                                            break;
                                        case "Public":
                                            transitCount[0]++;
                                            break;
                                        default:
                                            break;
                                    }
                                    break;

                                case "food":

                                    String foodType = entry.child("MealType").getValue(String.class);
                                    switch (foodType) {
                                        case "Fish":
                                            eatingFishCount[0]++;
                                            break;
                                        case "Vegetarian":
                                            eatingVegCount[0]++;
                                            break;
                                        default:
                                            break;
                                    }
                                    break;

                                default:
                                    break;
                            }

                            max[0] = Math.max(Math.max(Math.max(walkingCount[0], bikingCount[0]),
                                            Math.max(transitCount[0], minWaterBillCount[0])),
                                    Math.max(Math.max(minGasBillCount[0], minElectricityBillCount[0]),
                                            Math.max(eatingFishCount[0], eatingVegCount[0])));

                        }
                    }
                }

                //Whichever habit has subliminally occurred the most is the one suggested.
                for (int i = 0; i < counts.length; i++) {
                    if (max[0] == counts[i][0]) {
                        habitToSuggest[0] = habitNames[i];
                    }
                }

                //Display the suggested habit
                //Get habit description
                DatabaseReference habitRef = ref.child("Habits").child(habitToSuggest[0]);
                DatabaseReference habitDescRef = habitRef.child("Description");
                habitDescRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String habitDesc = snapshot.getValue(String.class);

                        //Get habit TextDisplay
                        DatabaseReference habitTDRef = habitRef.child("TextDisplay");
                        habitTDRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String habitTD = snapshot.getValue(String.class);

                                //Display everything
                                suggestedHabit.setText(habitToSuggest[0]);
                                suggestedHabitDesc.setText(habitDesc);
                                habitTextDisplay.setText(habitTD);
                                habitTotal.setText(max[0]);

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}