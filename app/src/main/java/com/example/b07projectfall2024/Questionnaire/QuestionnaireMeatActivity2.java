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
 * QuestionnaireMeatActivity2
 * This activity is the third of four responsible for calculating the user's diet emissions, and
 * the second of two responsible for calculating the user's emissions by meat, if they consume meat.
 * It navigates to QuestionnaireFoodActivity2
 */
public class QuestionnaireMeatActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_meat2);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double diet_emissions_rough = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //frequency options for chicken
        RadioButton daily = findViewById(R.id.daily);
        RadioButton frequently = findViewById(R.id.frequently);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton never = findViewById(R.id.never);
        //frequency options for fish/seafood
        RadioButton daily2 = findViewById(R.id.daily2);
        RadioButton frequently2 = findViewById(R.id.frequently2);
        RadioButton occasionally2 = findViewById(R.id.occasionally2);
        RadioButton never2 = findViewById(R.id.never2);

        //button next
        Button next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to final set of food questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;
                boolean status1 = false;
                boolean status2 = false;

                //Updating total emissions based on user response
                if (daily.isChecked()) {
                    emissions2 += 950;
                    status1 = true;
                } else if (frequently.isChecked()) {
                    emissions2 += 600;
                    status1 = true;
                } else if (occasionally.isChecked()) {
                    emissions2 += 200;
                    status1 = true;
                } else if (never.isChecked()) {
                    status1 = true;
                }

                if (daily2.isChecked()) {
                    emissions2 += 800;
                    status2 = true;
                } else if (frequently2.isChecked()) {
                    emissions2 += 500;
                    status2 = true;
                } else if (occasionally2.isChecked()) {
                    emissions2 += 150;
                    status2 = true;
                } else if (never2.isChecked()) {
                    status2 = true;
                }

                //Making sure user responded
                if (status1 && status2) {

                    //Update the current amount of emissions from diet
                    double diet_emissions = diet_emissions_rough + (emissions2 - emissions);

                    //Navigate to QuestionnaireFoodActivity2
                    Intent intent = new Intent(QuestionnaireMeatActivity2.this,
                            QuestionnaireFoodActivity2.class);
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