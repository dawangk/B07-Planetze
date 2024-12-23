package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.content.Intent;
import com.example.b07projectfall2024.R;

/**
 * QuestionnaireFoodActivity
 * This activity is the first of four that are responsible for recording the user's diet emissions
 * from the past year. It navigates to either QuestionnaireMeatActivity or
 * QuestionnaireFoodActivity2, depending on the user's responses
 */
public class QuestionnaireFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_food);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //Diet type options
        RadioButton meat = findViewById(R.id.meat);
        RadioButton vegetarian = findViewById(R.id.vegetarian);
        RadioButton pescatarian = findViewById(R.id.pescat);
        RadioButton vegan = findViewById(R.id.vegan);

        //next button
        Button next = findViewById(R.id.next);

        ////When next is clicked, do emission calculations and navigate to next activity
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;

                //If the user regularly eats meat, navigate to QuestionnaireMeatActivity
                if (meat.isChecked()) {
                    Intent intent = new Intent(QuestionnaireFoodActivity.this, QuestionnaireMeatActivity.class);
                    intent.putExtra("current_emissions", emissions2);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    intent.putExtra("flight_emissions", flight_emissions);
                    startActivity(intent);
                    finish();
                }

                //else, do emission calculations and navigate to QuestionnaireFoodActivity2
                else {

                    if (vegetarian.isChecked()) {
                        emissions2 += 1000;
                    } else if (vegan.isChecked()) {
                        emissions2 += 500;
                    } else if (pescatarian.isChecked()) {
                        emissions2 += 1500;
                    }

                    //Making sure user responded
                    if (emissions2 != emissions) {

                        //Storing the current emissions from diet
                        double diet_emissions = emissions2 - emissions;

                        //Navigating to QuestionnaireFoodActivity2
                        Intent intent = new Intent(QuestionnaireFoodActivity.this, QuestionnaireFoodActivity2.class);
                        intent.putExtra("current_emissions", emissions2);
                        intent.putExtra("diet_emissions", diet_emissions);
                        intent.putExtra("car_emissions", car_emissions);
                        intent.putExtra("transit_emissions", transit_emissions);
                        intent.putExtra("flight_emissions", flight_emissions);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}