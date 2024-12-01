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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Locale;
import java.time.format.DateTimeFormatter;


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
        TextView dateTextView = findViewById(R.id.date);
        DateFieldInit(dateTextView);

        TextView title = findViewById(R.id.title);
        TextView numHabitToday = findViewById(R.id.numHabitToday);
        TextView numHabitYday = findViewById(R.id.numHabitYday);

        Button dateUpdate = findViewById(R.id.dateUpdate);

        title.setText("Your progress towards " + habit + ":");

        //Displaying current day's habit progress
        displayHabit(CurrentSelectedDate, habit, numHabitToday, numHabitYday);

        //If date is changed, display new date's habit progress
        dateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayHabit(CurrentSelectedDate, habit, numHabitToday, numHabitYday);
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

    private void displayHabit(String CurrentSelectedDate, String habit, TextView numHabitToday, TextView numHabitYday) {
        DatabaseReference habitRef = ref.child("users").child(user.getUid()).child("Habits").child(habit);
        DatabaseReference habitToday = habitRef.child(CurrentSelectedDate);
        habitToday.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //If a habit entry exists for the day, display the number of occurrences
                int[] numToday = {0};
                if (snapshot.exists()) {
                    numToday[0] = snapshot.getValue(Integer.class);
                }

                numHabitToday.setText(Integer.toString(numToday[0]));

                String dayBefore = getDayBefore(CurrentSelectedDate);
                DatabaseReference habitYday = habitRef.child(dayBefore);
                //Compare to number of occurrences yesterday, if any exist.
                habitYday.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int[] numYday = {0};
                        if (snapshot.exists()) {
                            numYday[0] = snapshot.getValue(Integer.class);
                        }

                        int diff = numToday[0] - numYday[0];
                        String keyword = "more";
                        if (diff < 0) {
                            keyword = "less";
                        }

                        numHabitYday.setText(diff + " " + keyword);
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

    //Returns a format respresenting the day before date
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
}
