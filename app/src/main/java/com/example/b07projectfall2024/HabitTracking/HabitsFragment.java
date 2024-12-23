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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

        Button trackButton1 = rootView.findViewById(R.id.trackButton1);
        Button trackButton2 = rootView.findViewById(R.id.trackButton2);
        Button trackButton3 = rootView.findViewById(R.id.trackButton3);
        Button trackButton4 = rootView.findViewById(R.id.trackButton4);
        Button trackButton5 = rootView.findViewById(R.id.trackButton5);
        Button trackButton6 = rootView.findViewById(R.id.trackButton6);
        Button trackButton7 = rootView.findViewById(R.id.trackButton7);
        Button trackButton8 = rootView.findViewById(R.id.trackButton8);

        Button searchHabits = rootView.findViewById(R.id.searchHabits);
        Button suggestHabit = rootView.findViewById(R.id.suggestHabit);

        Spinner spinner = rootView.findViewById(R.id.Filter);

        TextView[] habits = {habit1, habit2, habit3, habit4, habit5, habit6, habit7, habit8};
        TextView[] adoptees = {adopt1, adopt2, adopt3, adopt4, adopt5, adopt6, adopt7, adopt8};
        String[][] habit_names = {{"Walking", "Taking the Transit", "Biking", "Eating Fish", "Eating Vegetarian",
                "Minimal Gas Bill", "Minimal Water Bill", "Minimal Electricity Bill"}};
        Button[] adopt_buttons = {adoptButton1, adoptButton2, adoptButton3, adoptButton4,
                adoptButton5, adoptButton6, adoptButton7, adoptButton8};
        Button[] track_buttons = {trackButton1, trackButton2, trackButton3, trackButton4,
                trackButton5, trackButton6, trackButton7, trackButton8};

        //Displaying the habits and their descriptions in their appropriate section
        displayHabits(habits, adoptees, adopt_buttons, track_buttons, habit_names[0]);

        //Initializing Spinners for habit filtering
        List<String> filters = Arrays.asList("Filters" ,"High", "Medium", "Low", "Transportation"
                , "Diet", "Consumption");
        ArrayAdapter<String> impactAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, filters);

        impactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(impactAdapter);
        spinner.setSelection(0);

        //Change the array of habits to only those with the matching impact
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "High":
                        checkImpact(habit_names[0],"High", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    case "Medium":
                        checkImpact(habit_names[0],"Medium", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    case "Low":
                        checkImpact(habit_names[0],"Low", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    case "Transportation":
                        checkType(habit_names[0],"Transportation", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    case "Diet":
                        checkType(habit_names[0],"Diet", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    case "Consumption":
                        checkType(habit_names[0],"Consumption", habits, adoptees, adopt_buttons, track_buttons);
                        break;
                    default:
                        displayHabits(habits, adoptees, adopt_buttons, track_buttons, habit_names[0]);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //The "adopt" buttons add the habit to the user's information in the database
        adoptButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[0], adopt_buttons[0]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[1], adopt_buttons[1]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[2], adopt_buttons[2]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[3], adopt_buttons[3]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[4], adopt_buttons[4]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[5], adopt_buttons[5]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[6], adopt_buttons[6]);
                loadFragment(new HabitsFragment());
            }
        });

        adoptButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adoptHabit(adoptees[7], adopt_buttons[7]);
                loadFragment(new HabitsFragment());
            }
        });

        //The "track progress" buttons redirect to a new fragment where the habit progress is displayed.
        trackButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[0].getText().toString().split("\n")[0]);
            }
        });

        trackButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[1].getText().toString().split("\n")[0]);
            }
        });

        trackButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[2].getText().toString().split("\n")[0]);
            }
        });

        trackButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[3].getText().toString().split("\n")[0]);
            }
        });

        trackButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[4].getText().toString().split("\n")[0]);
            }
        });

        trackButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[5].getText().toString().split("\n")[0]);
            }
        });

        trackButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[6].getText().toString().split("\n")[0]);
            }
        });

        trackButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(habits[7].getText().toString().split("\n")[0]);
            }
        });

        //Redirects to searchHabit fragment
        searchHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), HabitSearchActivity.class);
                startActivity(intent);
            }
        });

        //Redirects to suggestHabit fragment
        suggestHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SuggestHabitFragment());
            }
        });
    }

    private void displayHabits(TextView[] habits, TextView[] adoptees, Button[] adopt_buttons,
                               Button[] track_buttons, String[] habit_names) {

        int[] habit_index = {0};
        int[] adoptee_index = {0};

        for (String habit_name: habit_names) {

            //Checking if habit is currently being tracked
            DatabaseReference userHabitRef = ref.child("users").child(user.getUid())
                    .child("Habits").child(habit_name);
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

                                    //If habit is tracked, we display it's name and description in
                                    // the "Habits Currently being Tracked" section
                                    if (isTracked[0]) {
                                        habits[habit_index[0]].setText(habit_name + "\n"
                                                + habitDesc[0] + "\n");
                                        track_buttons[habit_index[0]].setVisibility(View.VISIBLE);
                                        habit_index[0]++;
                                    }
                                    //Else, it is displayed in the "Adopt a New Habit" section
                                    else {
                                        adoptees[adoptee_index[0]].setText(habit_name + "\n"
                                                + habitDesc[0] + "\n");
                                        adopt_buttons[adoptee_index[0]].setVisibility(View.VISIBLE);
                                        adoptee_index[0]++;
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

        //Clearing all others from screen (in case of filtering)
        for (int i = habit_index[0]; i < habits.length; i++) {
            habits[i].setText("");
            track_buttons[i].setVisibility(View.GONE);
        }
        for (int i = adoptee_index[0]; i < adoptees.length; i++) {
            adoptees[i].setText("");
            adopt_buttons[i].setVisibility(View.GONE);
        }

    }

    //Adopts the habit showcased in habitDisplay to the user's information in database,
    // and updates adoptButton to become disabled
    private void adoptHabit(TextView habitDisplay, Button adoptButton) {
        //Add the habit to the user's tracked habits in database
        DatabaseReference userRef = ref.child("users").child(user.getUid());
        DatabaseReference habitsRef = userRef.child("Habits");
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put(habitDisplay.getText().toString().split("\n")[0], 0);
        habitsRef.updateChildren(m);
        adoptButton.setText("Adopted!");
        adoptButton.setEnabled(false);
    }

    //Loads a new fragment with back navigation enabled
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showProgress(String habit) {
        Intent intent = new Intent(requireContext(), HabitProgressActivity.class);
        intent.putExtra("habit", habit);
        startActivity(intent);
    }

    private void checkImpact(String[] habitNames, String impact, TextView[] habits,
                             TextView[] adoptees, Button[] adopt_buttons, Button[] track_buttons) {

        ArrayList<String> matchingHabits = new ArrayList<>();
        for (String habit : habitNames) {
            DatabaseReference habitRef = ref.child("Habits").child(habit);

            habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Habit h = snapshot.getValue(Habit.class);
                    if (h.getImpact().equals(impact)) {
                        matchingHabits.add(habit);
                        Object[] matchingHabitsArray = matchingHabits.toArray();
                        String[] newHabitNames = new String[matchingHabitsArray.length];
                        for (int i = 0; i < newHabitNames.length; i++) {
                            newHabitNames[i] = (String) matchingHabitsArray[i];
                        }
                        displayHabits(habits, adoptees, adopt_buttons, track_buttons, newHabitNames);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void checkType(String[] habitNames, String type, TextView[] habits,
                             TextView[] adoptees, Button[] adopt_buttons, Button[] track_buttons) {

        ArrayList<String> matchingHabits = new ArrayList<>();
        for (String habit : habitNames) {
            DatabaseReference habitRef = ref.child("Habits").child(habit);

            habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Habit h = snapshot.getValue(Habit.class);
                    if (h.getKeywordOne().equals(type)) {
                        matchingHabits.add(habit);
                        Object[] matchingHabitsArray = matchingHabits.toArray();
                        String[] newHabitNames = new String[matchingHabitsArray.length];
                        for (int i = 0; i < newHabitNames.length; i++) {
                            newHabitNames[i] = (String) matchingHabitsArray[i];
                        }
                        displayHabits(habits, adoptees, adopt_buttons, track_buttons, newHabitNames);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
