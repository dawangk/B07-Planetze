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
 * QuestionnaireHousingActivity
 * This activity is the first of four responsible for recording the user's yearly housing emissions,
 * and navigates to QuestionnaireHousingActivity2
 */
public class QuestionnaireHousingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_housing);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //Type of house options
        RadioButton detached = findViewById(R.id.detached);
        RadioButton semi_detached = findViewById(R.id.semi_detached);
        RadioButton townhouse = findViewById(R.id.town);
        RadioButton condo = findViewById(R.id.apt);
        RadioButton other = findViewById(R.id.other);

        //Number of occupant options
        RadioButton one = findViewById(R.id.one);
        RadioButton two = findViewById(R.id.two);
        RadioButton three = findViewById(R.id.three);
        RadioButton five = findViewById(R.id.five);

        //next button
        Button next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to next set of questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = "";
                int num_people = 0;

                //Storing responses for the main calculations in QuestionnaireHousingActivity4
                if (detached.isChecked()) {
                    type = "detached";
                } else if (semi_detached.isChecked()) {
                    type = "semi-detached";
                } else if (townhouse.isChecked()) {
                    type = "townhouse";
                } else if (condo.isChecked()) {
                    type = "condo";
                } else if (other.isChecked()) {
                    //"other" should be treated as townhouse, by PlanetZe requirements.
                    type = "townhouse";
                }

                if (one.isChecked()) {
                    num_people = 1;
                } else if (two.isChecked()) {
                    num_people = 2;
                } else if (three.isChecked()) {
                    num_people = 3;
                } else if (five.isChecked()) {
                    num_people = 5;
                }

                //Making sure user responded
                if (!type.isEmpty() && num_people != 0) {

                    //Navigating to QuestionnaireHousingActivity2
                    Intent intent = new Intent(QuestionnaireHousingActivity.this,
                            QuestionnaireHousingActivity2.class);
                    intent.putExtra("current_emissions", emissions);
                    intent.putExtra("house_type", type);
                    intent.putExtra("num_people", num_people);
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