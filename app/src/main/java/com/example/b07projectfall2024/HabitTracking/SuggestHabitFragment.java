package com.example.b07projectfall2024.HabitTracking;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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

        Button startTracking = rootView.findViewById(R.id.startTracking);

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
        String[] habitNames = {"Minimal Water Bill", "Minimal Gas Bill", "Minimal Electricity Bill"
                ,"Walking", "Biking", "Taking the Transit", "Eating Fish", "Eating Vegetarian"};

        DatabaseReference userRef = ref.child("users").child(user.getUid());

        //Only considering habits that are not currently being tracked
        ArrayList<String> removedHabitNames = new ArrayList<>();
        DatabaseReference userHabitsRef = userRef.child("Habits");
        userHabitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over habits tracked by user and appending them to newHabitNames
                for (DataSnapshot habit : snapshot.getChildren()) {
                    String habitName = habit.getKey();
                    removedHabitNames.add(habitName);
                }

                //If all habits are already tracked, we inform them
                if (removedHabitNames.size() == habitNames.length) {
                    loadFragment(new noHabitsAvailableFragment());
                }

                String[] habitToSuggest = {""};
                DatabaseReference entriesRef = userRef.child("entries");
                TextView[] display = {suggestedHabit, suggestedHabitDesc, habitTextDisplay, habitTotal};

                //Looping over all user entries and counting them
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
                                                            String habit = habitNames[0];
                                                            if (!removedHabitNames.contains(habit)) {
                                                                minWaterBillCount[0]++;
                                                                displayMaxHabit(minWaterBillCount[0], max, habit, display);
                                                            }
                                                            break;
                                                        case "Gas":
                                                            habit = habitNames[1];
                                                            if (!removedHabitNames.contains(habit)) {
                                                                minGasBillCount[0]++;
                                                                displayMaxHabit(minGasBillCount[0], max, habit, display);
                                                            }
                                                            break;
                                                        case "Electricity":
                                                            habit = habitNames[2];
                                                            if (!removedHabitNames.contains(habit)) {
                                                                minElectricityBillCount[0]++;
                                                                displayMaxHabit(minElectricityBillCount[0], max, habit, display);
                                                            }
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
                                                    String habit = habitNames[3];
                                                    if (!removedHabitNames.contains(habit)) {
                                                        walkingCount[0]++;
                                                        displayMaxHabit(walkingCount[0], max, habit, display);
                                                    }
                                                    break;
                                                case "Cycled":
                                                    habit = habitNames[4];
                                                    if (!removedHabitNames.contains(habit)) {
                                                        bikingCount[0]++;
                                                        displayMaxHabit(bikingCount[0], max, habit, display);
                                                    }
                                                    break;
                                                case "Public":
                                                    habit = habitNames[5];
                                                    if (!removedHabitNames.contains(habit)) {
                                                        transitCount[0]++;
                                                        displayMaxHabit(transitCount[0], max, habit, display);
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;

                                        case "food":

                                            String foodType = entry.child("MealType").getValue(String.class);
                                            switch (foodType) {
                                                case "Fish":
                                                    String habit = habitNames[6];
                                                    if (!removedHabitNames.contains(habit)) {
                                                        eatingFishCount[0]++;
                                                        displayMaxHabit(eatingFishCount[0], max, habit, display);
                                                    }
                                                    break;
                                                case "Vegetarian":
                                                    habit = habitNames[7];
                                                    if (!removedHabitNames.contains(habit)) {
                                                        eatingVegCount[0]++;
                                                        displayMaxHabit(eatingVegCount[0], max, habit, display);
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;

                                        default:
                                            break;
                                    }

                                }
                            }
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

        //startTracking should start tracking the habit and redirect to HabitProgressActivity
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Begin tracking
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                String habit = suggestedHabit.getText().toString();
                m.put(habit, 0);
                habitsRef.updateChildren(m);

                //Redirect
                Intent intent = new Intent(requireContext(), HabitProgressActivity.class);
                intent.putExtra("habit", habit);
                startActivity(intent);
            }
        });

    }

    private void displayMaxHabit(int count, int[] currentMax, String habit, TextView[] display) {
        if (count > currentMax[0]) {
            currentMax[0] = count;

            //Getting habit info
            DatabaseReference habitRef = ref.child("Habits").child(habit);
            habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Habit habit = snapshot.getValue(Habit.class);
                    String habitName = habit.getName();
                    String habitDesc = habit.getDescription();
                    String habitTD = habit.getTextDisplay();

                    //Display everything
                    display[0].setText(habitName);
                    display[1].setText(habitDesc);
                    display[2].setText(habitTD + " ");
                    display[3].setText(Integer.toString(currentMax[0]));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Loads a new fragment with back navigation enabled
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}