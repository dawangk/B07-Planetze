package com.example.b07projectfall2024.HabitTracking;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.NavigationBar.DashboardFragment;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.fragment.app.Fragment;

/**
 * This activity takes the responsibility of displaying the progress a user has achieved with
 * respect the a particular habit determined from the calling activity/fragment
 */
public class HabitProgressActivity extends AppCompatActivity {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String CurrentSelectedDate;
    Context currentContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habit_progress);

        Intent intent = getIntent();
        String habit = intent.getStringExtra("habit");

        currentContext = HabitProgressActivity.this;

        //Date selector
        TextView dateTextView = findViewById(R.id.date);
        DateFieldInit(dateTextView);

        //Habit name
        TextView title = findViewById(R.id.title);

        //Habit completion counts for the day
        TextView numHabitToday = findViewById(R.id.numHabitToday);
        TextView numHabitYday = findViewById(R.id.numHabitYday);

        //Anti-habit completion counts for the day
        TextView numAntiHabitToday = findViewById(R.id.numAntiHabitToday);
        TextView numAntiHabitYday = findViewById(R.id.numAntiHabitYday);

        //More text displays
        TextView habitTextDisplay = findViewById(R.id.habitTextDisplay);
        TextView antHabitTextDisplay = findViewById(R.id.antiHabitTextDisplay);
        TextView habitProgress = findViewById(R.id.habitProgress);
        TextView antiHabitProgress = findViewById(R.id.antiHabitProgress);
        TextView habitTextDisplay2= findViewById(R.id.habitTextDisplay2);
        TextView antiHabitTextDisplay2 = findViewById(R.id.antiHabitTextDisplay2);

        //Total amount of habit completed since tracking
        TextView numHabitTotal = findViewById(R.id.numHabit);

        //Total amount of anti-habit completed since tracking
        TextView numAntiHabitTotal = findViewById(R.id.numAntiHabit);

        //date update button
        Button dateUpdate = findViewById(R.id.dateUpdate);

        //stop tracking button
        Button stopTracking = findViewById(R.id.stopTracking);

        //Checking if habit is being tracked
        DatabaseReference userHabitRef = ref.child("users").child(user.getUid())
                .child("Habits").child(habit);

        String[] antiHabit = {""};
        DatabaseReference antiHabitRef = ref.child("Habits").child(habit)
                .child("AntiHabit");

        userHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If it is being tracked, we display the user's progress
                if (snapshot.exists()) {
                    //Getting name of the corresponding anti-habit
                    antiHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            antiHabit[0] = snapshot.getValue(String.class);
                            //Displaying current day's habit progress
                            displayHabit(CurrentSelectedDate, habit, numHabitToday, numHabitYday,
                                    "Habits", habitProgress);
                            displayHabit(CurrentSelectedDate, antiHabit[0], numAntiHabitToday,
                                    numAntiHabitYday, "AntiHabits", antiHabitProgress);
                            setTextDisplay(habit, antiHabit[0], habitTextDisplay,
                                    antHabitTextDisplay);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                //Else, navigate to notYetTrackedActivity
                else {
                    Intent intent = new Intent(HabitProgressActivity.this,
                            NotYetTrackedActivity.class);
                    intent.putExtra("habit", habit);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Setting the title
        title.setText("Your progress towards " + habit + ":");

        //If date is changed, display new date's habit progress
        dateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayHabit(CurrentSelectedDate, habit, numHabitToday, numHabitYday,
                        "Habits", habitProgress);
                displayHabit(CurrentSelectedDate, antiHabit[0], numAntiHabitToday, numAntiHabitYday,
                        "AntiHabits", antiHabitProgress);
            }
        });

        //Displaying the total count of both the habit and anti. since the tracking period begun
        antiHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String antiHabit = snapshot.getValue(String.class);

                displayAll(habit, numHabitTotal, "Habits");
                displayAll(antiHabit, numAntiHabitTotal, "AntiHabits");
                setTextDisplay(habit, antiHabit, habitTextDisplay2, antiHabitTextDisplay2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //If user chooses to stop tracking the habit, we delete all habit data from the database
        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Navigating back to HabitsFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HabitsFragment())
                        .commit();

                //Stop tracking the habit, delete all data
                DatabaseReference UserHRef = ref.child("users").child(user.getUid())
                        .child("Habits").child(habit);
                UserHRef.removeValue();

                //Find associated anti-habit
                DatabaseReference antiHabitRef = ref.child("Habits").child(habit)
                        .child("AntiHabit");
                antiHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String antiHabit = snapshot.getValue(String.class);
                        DatabaseReference UserARef = ref.child("users")
                                .child(user.getUid()).child("AntiHabits").child(antiHabit);

                        //Only remove anti-habit if a tracking instance exists
                        UserARef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserARef.removeValue();
                                }

                                Intent intent = new Intent(HabitProgressActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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
        });
    }

    //Initializes DateField allowing users to select any date
    private void DateFieldInit(TextView dateTextView) {
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

    /**
     * Display the habit's count of completions for the current selected date and previous date
     * @param CurrentSelectedDate The current selected date to check entries for
     * @param habit The habit being considered
     * @param numHabitToday The TextView to which the completions for the current day are displayed
     * @param numHabitYday The TextView to which the completions for the previous day are displayed
     * @param type Indicates whether it is a "Habit" or "AntiHabit"
     * @param habitProgress
     */
    private void displayHabit(String CurrentSelectedDate, String habit, TextView numHabitToday,
                              TextView numHabitYday, String type, TextView habitProgress) {
        DatabaseReference habitRef = ref.child("users").child(user.getUid()).child(type)
                .child(habit);
        DatabaseReference habitToday = habitRef.child(CurrentSelectedDate);
        habitToday.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If a habit entry exists for the day, display the number of occurrences
                int[] numToday = {0};
                if (snapshot.exists()) {
                    numToday[0] = snapshot.getValue(Integer.class);
                    numHabitToday.setText(Integer.toString(numToday[0]));
                } else {
                    numHabitToday.setText("0");
                }

                String dayBefore = getDayBefore(CurrentSelectedDate);
                DatabaseReference habitYday = habitRef.child(dayBefore);
                //Compare to number of occurrences yesterday, if any exist.
                habitYday.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int[] numYday = {0};
                        if (snapshot.exists()) {
                            numYday[0] = snapshot.getValue(Integer.class);

                            int diff = numToday[0] - numYday[0];
                            String keyword = "more";
                            if (diff < 0) {
                                keyword = "less";
                            }
                            numHabitYday.setText(Math.abs(diff) + " " + keyword);

                            displayProgress(diff, habitProgress, type.equals("Habits"));
                        }

                        else {
                            numHabitYday.setText(numToday[0] + " more");

                            displayProgress(numToday[0], habitProgress, type.equals("Habits"));
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

    private void setTextDisplay(String habit, String antiHabit, TextView habitTextDisplay,
                                TextView antiHabitTextDisplay) {

        //Setting the TextDisplay of habit
        DatabaseReference habitRef = ref.child("Habits").child(habit)
                .child("TextDisplay");
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String habitText = snapshot.getValue(String.class);
                habitTextDisplay.setText(habitText + " ");

                //Setting the TextDisplay of antiHabit
                DatabaseReference antiHabitRef = ref.child("AntiHabits").child(antiHabit)
                        .child("TextDisplay");
                antiHabitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String antiHabitText = snapshot.getValue(String.class);
                        antiHabitTextDisplay.setText(antiHabitText + " ");
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

    private void displayProgress(int diff, TextView progress, boolean good) {

        if (diff > 0 && good) {
            progress.setText("This progress is great!");
        } else if (diff > 0 || good) {
            progress.setText("Oh no! You did better on the previous day.");
        } else {
            progress.setText("This progress is great!");
        }
    }

    //Displays the total number of times habit has been completed since tracking began, to numHabit
    private void displayAll(String habit, TextView numHabit, String type) {
        DatabaseReference habitRef = ref.child("users").child(user.getUid()).child(type)
                .child(habit);
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    total += childSnapshot.getValue(Integer.class);
                    numHabit.setText(Integer.toString(total));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //Returns a string representing the day before date
    private String getDayBefore(String date) {
        // Parse the date components
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(8, 10));

        //Increment day back by 1. If it is 0, we go to the previous month. If previous month is 0, we go to prev. year.
        day--;
        if (day == 0) {
            month--;
            if (month == 0) {
                month = 12;
                year--;
            }
            //Set day to the last day of the new month
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    day = 31;
                case 4:
                case 6:
                case 9:
                case 11:
                    day = 30;
                case 2:
                    if (year % 4 == 0 && year % 100 != 0) {
                        day = 29;
                    } else {
                        day = 28;
                    }
            }
        }
        //Return the string in the YYYY-MM-DD format we are working in
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
