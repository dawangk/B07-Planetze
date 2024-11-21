package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;

import com.example.b07projectfall2024.R;

public class QuestionnaireConsumptionActivity2 extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_consumption2);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double consumption_emissions_rough = intent.getDoubleExtra("consumption_emissions", 0.0);
        double housing_emissions = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        RadioButton less = findViewById(R.id.less);
        RadioButton min = findViewById(R.id.min);
        RadioButton mid = findViewById(R.id.mid);
        RadioButton more = findViewById(R.id.more);
        RadioButton max = findViewById(R.id.max);
        RadioButton never = findViewById(R.id.never);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton frequently = findViewById(R.id.frequently);
        RadioButton always = findViewById(R.id.always);

        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double consumption_emissions = consumption_emissions_rough;
                boolean status1 = false;
                boolean status2 = false;

                //Updating consumption emissions based on user response
                if (less.isChecked()) {
                    consumption_emissions += 300;
                    status1 = true;
                } else if (mid.isChecked()) {
                    consumption_emissions += 600;
                    status1 = true;
                } else if (more.isChecked()) {
                    consumption_emissions += 900;
                    status1 = true;
                } else if (max.isChecked()) {
                    consumption_emissions += 1200;
                    status1 = true;
                } else if (min.isChecked()) {
                    status1 = true;
                }

                if (occasionally.isChecked()) {
                    consumption_emissions -= 54;
                    status2 = true;
                } else if (frequently.isChecked()) {
                    consumption_emissions -= 108;
                    status2 = true;
                } else if (always.isChecked()) {
                    consumption_emissions -= 180;
                    status2 = true;
                } else if (never.isChecked()) {
                    status2 = true;
                }

                //Checking if user responded
                if (status1 && status2) {

                    double total_emissions = emissions + consumption_emissions;

                    //Moving to results page
                    Intent intent = new Intent(QuestionnaireConsumptionActivity2.this, DisplayResultsActivity.class);
                    intent.putExtra("final_emissions", total_emissions);
                    intent.putExtra("diet_emissions", diet_emissions);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    intent.putExtra("flight_emissions", flight_emissions);
                    intent.putExtra("housing_emissions", housing_emissions);
                    intent.putExtra("consumption_emissions", consumption_emissions);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}