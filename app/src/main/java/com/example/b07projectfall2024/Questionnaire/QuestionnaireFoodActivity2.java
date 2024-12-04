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
 * QuestionnaireMeatActivity
 * This activity is the final of four responsible for calculating the user's diet emissions, and
 * navigates to QuestionnaireHousingActivity
 */
public class QuestionnaireFoodActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_food2);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double diet_emissions_rough = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //wasting leftovers frequency
        RadioButton never = findViewById(R.id.never);
        RadioButton rarely = findViewById(R.id.rarely);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton frequently = findViewById(R.id.frequently);

        //next button
        Button next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to set of housing questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;

                //Updating total emissions based on user response
                if (rarely.isChecked()) {
                    emissions2 += 23.4;
                } else if (occasionally.isChecked()) {
                    emissions2 += 70.2;
                } else if (frequently.isChecked()) {
                    emissions2 += 140.4;
                }

                //Making sure user responded
                if (emissions2 != emissions || never.isChecked()) {

                    //Finalizing the user's emissions by diet
                    double diet_emissions = diet_emissions_rough+ (emissions2 - emissions);

                    //Navigating to QuestionnaireHousingActivity
                    Intent intent = new Intent(QuestionnaireFoodActivity2.this, QuestionnaireHousingActivity.class);
                    intent.putExtra("current_emissions", emissions2);
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