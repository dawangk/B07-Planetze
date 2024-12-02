package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;
import com.example.b07projectfall2024.R;

public class QuestionnaireHousingActivity2 extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_housing2);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        String house_type = intent.getStringExtra("house_type");
        int num_people = intent.getIntExtra("num_people", 0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        RadioButton min = findViewById(R.id.min);
        RadioButton mid = findViewById(R.id.mid);
        RadioButton max = findViewById(R.id.max);
        RadioButton gas = findViewById(R.id.gas);
        RadioButton electricity = findViewById(R.id.electricity);
        RadioButton oil = findViewById(R.id.oil);
        RadioButton propane = findViewById(R.id.propane);
        RadioButton wood = findViewById(R.id.wood);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String size = "";
                String energy_type = "";

                //Asking and storing the responses of prerequisite questions for main question in housing 3
                if (min.isChecked()) {
                    size = "min_size";
                } else if (mid.isChecked()) {
                    size = "mid_size";
                } else if (max.isChecked()) {
                    size = "max_size";
                }

                if (gas.isChecked()) {
                    energy_type = "natural gas";
                } else if (electricity.isChecked()) {
                    energy_type = "electricity";
                } else if (oil.isChecked()) {
                    energy_type = "oil";
                } else if (propane.isChecked()) {
                    energy_type = "propane";
                } else if (wood.isChecked()) {
                    energy_type = "wood";
                }

                //Checking if user responded
                if (!size.isEmpty() && !energy_type.isEmpty()) {

                    //Moving to third set of house related questions
                    Intent intent = new Intent(QuestionnaireHousingActivity2.this, QuestionnaireHousingActivity3.class);
                    intent.putExtra("current_emissions", emissions);
                    intent.putExtra("house_type", house_type);
                    intent.putExtra("num_people", num_people);
                    intent.putExtra("house_size", size);
                    intent.putExtra("energy_type", energy_type);
                    intent.putExtra("diet_emissions", diet_emissions);
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