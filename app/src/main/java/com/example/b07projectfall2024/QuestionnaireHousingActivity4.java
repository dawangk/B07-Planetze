package com.example.b07projectfall2024;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;;

public class QuestionnaireHousingActivity4 extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_housing4);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double housing_emissions_rough = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        RadioButton primarily = findViewById(R.id.primarily);
        RadioButton partially = findViewById(R.id.partially);
        RadioButton no = findViewById(R.id.no);

        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;

                //Updating total emissions based on user response
                if (primarily.isChecked()) {
                    emissions2 -= 6000;
                } else if (partially.isChecked()) {
                    emissions2 -= 4000;
                }

                //Checking if user responded
                if (emissions2 != emissions || no.isChecked()) {

                    //FINAL: Storing the emissions from housing
                    double housing_emissions = housing_emissions_rough + (emissions2 - emissions);

                    //Moving to next set of questions about consumption
                    Intent intent = new Intent(QuestionnaireHousingActivity4.this, QuestionnaireConsumptionActivity.class);
                    intent.putExtra("current_emissions", emissions2);
                    intent.putExtra("diet_emissions", diet_emissions);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    intent.putExtra("flight_emissions", flight_emissions);
                    intent.putExtra("housing_emissions", housing_emissions);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}