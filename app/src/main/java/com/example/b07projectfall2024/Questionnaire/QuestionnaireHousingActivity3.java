package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;
import com.example.b07projectfall2024.R;

/**
 * QuestionnaireHousingActivity3
 * This activity is the third of four responsible for recording the user's yearly housing
 * emissions, and navigates to QuestionnaireHousingActivity4.
 */
public class QuestionnaireHousingActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_housing3);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        String house_type = intent.getStringExtra("house_type");
        int num_people = intent.getIntExtra("num_people", 0);
        String house_size = intent.getStringExtra("house_size");
        String energy_type = intent.getStringExtra("energy_type");
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //Bill price options
        RadioButton min = findViewById(R.id.min);
        RadioButton less = findViewById(R.id.less);
        RadioButton mid = findViewById(R.id.mid);
        RadioButton more = findViewById(R.id.more);
        RadioButton max = findViewById(R.id.max);
        RadioButton gas = findViewById(R.id.gas);

        //Heat type options
        RadioButton electricity = findViewById(R.id.electricity);
        RadioButton oil = findViewById(R.id.oil);
        RadioButton propane = findViewById(R.id.propane);
        RadioButton solar = findViewById(R.id.solar);
        RadioButton other = findViewById(R.id.other);

        //next button
        Button next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to next set of questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int monthly_bill = 0;
                String energy_type2 = "";

                //Storing responses for the main calculations in QuestionnaireHousingActivity4
                if (min.isChecked()) {
                    monthly_bill = 50;
                } else if (less.isChecked()) {
                    monthly_bill = 100;
                } else if (mid.isChecked()) {
                    monthly_bill = 150;
                } else if (more.isChecked()) {
                    monthly_bill = 200;
                } else if (max.isChecked()) {
                    monthly_bill = 250;
                }

                if (gas.isChecked()) {
                    energy_type2 = "natural gas";
                } else if (electricity.isChecked()) {
                    energy_type2 = "electricity";
                } else if (oil.isChecked()) {
                    energy_type2 = "oil";
                } else if (propane.isChecked()) {
                    energy_type2 = "propane";
                } else if (solar.isChecked()) {
                    energy_type2 = "solar";
                } else if (other.isChecked()) {
                    energy_type2 = "other";
                }

                //Making sure user responded
                if (monthly_bill != 0 && !energy_type2.isEmpty()) {

                    //Navigating to QuestionnaireActivity4, with all obtained data thus far
                    Intent intent = new Intent(QuestionnaireHousingActivity3.this, QuestionnaireHousingActivity4.class);
                    intent.putExtra("current_emissions", emissions);
                    intent.putExtra("diet_emissions", diet_emissions);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    intent.putExtra("flight_emissions", flight_emissions);
                    intent.putExtra("house_type", house_type);
                    intent.putExtra("num_people", num_people);
                    intent.putExtra("house_size", house_size);
                    intent.putExtra("energy_type", energy_type);
                    intent.putExtra("energy_type2", energy_type2);
                    intent.putExtra("monthly_bill", monthly_bill);

                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}