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
 * QuestionnaireConsumptionActivity
 * This activity is the first of two responsible for recording the user's consumption emissions,
 * and navigates to QuestionnaireConsumptionActivity2
 */
public class QuestionnaireConsumptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_consumption);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double housing_emissions = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //New clothes frequency options
        RadioButton monthly = findViewById(R.id.monthly);
        RadioButton quarterly = findViewById(R.id.quarterly);
        RadioButton annually = findViewById(R.id.annually);
        RadioButton rarely = findViewById(R.id.rarely);

        //Second-hand/eco-friendly frequency options
        RadioButton regularly = findViewById(R.id.regularly);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton no = findViewById(R.id.no);

        //next button
        Button next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to next set of questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double amount = 0;
                double rate = 0;

                //Updating total emissions based on user response
                if (monthly.isChecked()) {
                    amount = 360;
                } else if (quarterly.isChecked()) {
                    amount = 120;
                } else if (annually.isChecked()) {
                    amount = 100;
                } else if (rarely.isChecked()) {
                    amount = 5;
                }

                if (regularly.isChecked()) {
                    rate = 0.5;
                } else if (occasionally.isChecked()) {
                    rate = 0.7;
                } else if (no.isChecked()) {
                    rate = 1;
                }

                //Current consumption emissions
                double consumption_emissions = amount * rate;

                //Making sure user responded
                if (consumption_emissions != 0) {

                    //Navigating to QuestionnaireConsumptionActivity2
                    Intent intent = new Intent(QuestionnaireConsumptionActivity.this, QuestionnaireConsumptionActivity2.class);
                    intent.putExtra("current_emissions", emissions);
                    intent.putExtra("consumption_emissions", consumption_emissions);
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