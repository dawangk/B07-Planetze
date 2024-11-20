package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;;import com.example.b07projectfall2024.R;

public class QuestionnaireFlightActivity extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_flight);

        RadioButton never = findViewById(R.id.never);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton frequently = findViewById(R.id.frequently);
        RadioButton supfrequently = findViewById(R.id.supfrequently);
        RadioButton always = findViewById(R.id.always);
        RadioButton never2 = findViewById(R.id.never2);
        RadioButton occasionally2 = findViewById(R.id.occasionally2);
        RadioButton frequently2 = findViewById(R.id.frequently2);
        RadioButton supfrequently2 = findViewById(R.id.supfrequently2);
        RadioButton always2 = findViewById(R.id.always2);
        next = findViewById(R.id.next);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;
                boolean status1 = false;
                boolean status2 = false;

                //Updating total emissions based on user response
                if (occasionally.isChecked()) {
                    emissions2 += 225;
                    status1 = true;
                } else if (frequently.isChecked()) {
                    emissions2 += 600;
                    status1 = true;
                } else if (supfrequently.isChecked()) {
                    emissions2 += 1200;
                    status1 = true;
                } else if (always.isChecked()) {
                    emissions2 += 1800;
                    status1 = true;
                } else if (never.isChecked()) {
                    status1 = true;
                }

                if (occasionally2.isChecked()) {
                    emissions2 += 825;
                    status2 = true;
                } else if (frequently2.isChecked()) {
                    emissions2 += 2200;
                    status2 = true;
                } else if (supfrequently2.isChecked()) {
                    emissions2 += 4400;
                    status2 = true;
                } else if (always2.isChecked()) {
                    emissions2 += 6600;
                    status2 = true;
                } else if (never2.isChecked()) {
                    status2 = true;
                }

                //Checking if user responded
                if (status1 && status2) {

                    //Storing the amount of emissions that come from flights
                    double flight_emissions = emissions2 - emissions;

                    //Moving to next set of questions about food
                    Intent intent = new Intent(QuestionnaireFlightActivity.this, QuestionnaireFoodActivity.class);
                    intent.putExtra("current_emissions", emissions2);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    intent.putExtra("flight_emissions", flight_emissions);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}