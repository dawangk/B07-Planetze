package com.example.b07projectfall2024.HabitTracking;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.b07projectfall2024.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;

public class HabitsFragment extends Fragment {

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    public HabitsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_habits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        //Habits currently being tracked
        TextView habit1 = rootView.findViewById(R.id.habit1);
        TextView habit2 = rootView.findViewById(R.id.habit2);
        TextView habit3 = rootView.findViewById(R.id.habit3);
        TextView habit4 = rootView.findViewById(R.id.habit4);
        TextView habit5 = rootView.findViewById(R.id.habit5);
        TextView habit6 = rootView.findViewById(R.id.habit6);
        TextView habit7 = rootView.findViewById(R.id.habit7);
        TextView habit8 = rootView.findViewById(R.id.habit8);

        //Habits available to adopt
        TextView adopt1 = rootView.findViewById(R.id.adopt1);
        TextView adopt2 = rootView.findViewById(R.id.adopt2);
        TextView adopt3 = rootView.findViewById(R.id.adopt3);
        TextView adopt4 = rootView.findViewById(R.id.adopt4);
        TextView adopt5 = rootView.findViewById(R.id.adopt5);
        TextView adopt6 = rootView.findViewById(R.id.adopt6);
        TextView adopt7 = rootView.findViewById(R.id.adopt7);
        TextView adopt8 = rootView.findViewById(R.id.adopt8);

        Button adoptButton1 = rootView.findViewById(R.id.adoptButton1);
        Button adoptButton2 = rootView.findViewById(R.id.adoptButton2);
        Button adoptButton3 = rootView.findViewById(R.id.adoptButton3);
        Button adoptButton4 = rootView.findViewById(R.id.adoptButton4);
        Button adoptButton5 = rootView.findViewById(R.id.adoptButton5);
        Button adoptButton6 = rootView.findViewById(R.id.adoptButton6);
        Button adoptButton7 = rootView.findViewById(R.id.adoptButton7);
        Button adoptButton8 = rootView.findViewById(R.id.adoptButton8);

        TextView[] habits = {habit1, habit2, habit3, habit4, habit5, habit6, habit7, habit8};
        TextView[] adoptees = {adopt1, adopt2, adopt3, adopt4, adopt5, adopt6, adopt7, adopt8};
        String[] habit_names = {"Walking", "Transit", "Biking", "EatingFish", "EatingVeggies",
                "MinimalGasBill", "MinimalWaterBill", "MinimalElectricityBill"};
        Button[] adopt_buttons = {adoptButton1, adoptButton2, adoptButton3, adoptButton4,
                adoptButton5, adoptButton6, adoptButton7, adoptButton8};

        int[] habit_index = {0};
        int[] adoptee_index = {0};
        int[] button_index = {0};

        //Displaying the habits and their descriptions in their appropriate section
        for (String habit_name: habit_names) {

            //Checking if habit is currently being tracked
            DatabaseReference userHabitRef = ref.child("users").child(user.getUid()).child("Habits").child(habit_name);
            DatabaseReference habitRef = ref.child("Habits").child(habit_name);

            //Getting habit info
            habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String[] habitDesc = {""};
                    //Getting habit description
                    DatabaseReference habitDescRef = habitRef.child("Description");
                    habitDescRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            habitDesc[0] = snapshot.getValue(String.class);

                            //Checking if habit is already being tracked
                            boolean[] isTracked = {false};
                            userHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        isTracked[0] = true;
                                    }

                                    //If habit is tracked, we display it's name and description in the "Habits Currently being Tracked" section
                                    if (isTracked[0]) {
                                        habits[habit_index[0]].setText(habit_name + "\n" + habitDesc[0] + "\n");
                                        habit_index[0]++;
                                    }
                                    //Else, it is displayed in the "Adopt a New Habit" section
                                    else {
                                        adoptees[adoptee_index[0]].setText(habit_name + "\n" + habitDesc[0] + "\n");
                                        adoptee_index[0]++;
                                        adopt_buttons[button_index[0]].setVisibility(View.VISIBLE);
                                        button_index[0]++;

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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        //The "adopt" buttons add the habit to the user's information in the database
        adoptButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[0].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[0].setText("Adopted!");
                adopt_buttons[0].setEnabled(false);
            }
        });

        adoptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[1].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[1].setText("Adopted!");
                adopt_buttons[1].setEnabled(false);
            }
        });

        adoptButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[2].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[2].setText("Adopted!");
                adopt_buttons[2].setEnabled(false);
            }
        });

        adoptButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[3].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[3].setText("Adopted!");
                adopt_buttons[3].setEnabled(false);
            }
        });

        adoptButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[4].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[4].setText("Adopted!");
                adopt_buttons[4].setEnabled(false);
            }
        });

        adoptButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[5].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[5].setText("Adopted!");
                adopt_buttons[5].setEnabled(false);
            }
        });

        adoptButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[6].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[6].setText("Adopted!");
                adopt_buttons[6].setEnabled(false);
            }
        });

        adoptButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add the habit to the user's tracked habits in database
                DatabaseReference userRef = ref.child("users").child(user.getUid());
                DatabaseReference habitsRef = userRef.child("Habits");
                HashMap<String, Object> m = new HashMap<String, Object>();
                m.put(adoptees[7].getText().toString().split("\n")[0], 0);
                habitsRef.updateChildren(m);
                adopt_buttons[7].setText("Adopted!");
                adopt_buttons[7].setEnabled(false);
            }
        });
    }
}
