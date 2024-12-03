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
 * QuestionnaireCarActivity
 * This activity is started if the user drives a vehicle, and is responsible for recording their
 * vehicle emissions from the past year, and navigates to QuestionnaireTransitActivity
 */
public class QuestionnaireCarActivity extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_car);

        //Car type options
        RadioButton gasoline = findViewById(R.id.gasoline);
        RadioButton diesel = findViewById(R.id.diesel);
        RadioButton hybrid = findViewById(R.id.hybrid);
        RadioButton electric = findViewById(R.id.electric);

        //Distance driven options
        RadioButton option1 = findViewById(R.id.option1);
        RadioButton option2 = findViewById(R.id.option2);
        RadioButton option3 = findViewById(R.id.option3);
        RadioButton option4 = findViewById(R.id.option4);
        RadioButton option5 = findViewById(R.id.option5);
        RadioButton option6 = findViewById(R.id.option6);
        next = findViewById(R.id.next);

        //When next is clicked, do emission calculations and navigate to transit emission questions
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double rate = 0;
                double distance = 0;

                //Updating the total emissions based on user response
                if (gasoline.isChecked()) {
                    rate = 0.24;
                } else if (diesel.isChecked()) {
                    rate = 0.27;
                } else if (hybrid.isChecked()) {
                    rate = 0.16;
                } else if (electric.isChecked()) {
                    rate = 0.05;
                }

                if (option1.isChecked()) {
                    distance = 5000;
                } else if (option2.isChecked()) {
                    distance = 10000;
                } else if (option3.isChecked()) {
                    distance = 15000;
                } else if (option4.isChecked()) {
                    distance = 20000;
                } else if (option5.isChecked()) {
                    distance = 25000;
                } else if (option6.isChecked()) {
                    distance = 35000;
                }

                double emissions = rate * distance;

                //Making sure user responded
                if (emissions != 0) {

                    //Storing the amount of emissions that come from transit
                    double car_emissions = emissions;

                    //Navigating to QuestionnaireTransitActivity
                    Intent intent = new Intent(QuestionnaireCarActivity.this,
                            QuestionnaireTransitActivity.class);
                    intent.putExtra("current_emissions", emissions);
                    intent.putExtra("car_emissions", car_emissions);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}