package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;;import com.example.b07projectfall2024.R;


public class QuestionnaireTransitActivity extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_transit);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);

        RadioButton never = findViewById(R.id.never);
        RadioButton occasionally = findViewById(R.id.occasionally);
        RadioButton frequently = findViewById(R.id.frequently);
        RadioButton always = findViewById(R.id.always);
        RadioButton option1 = findViewById(R.id.option1);
        RadioButton option2 = findViewById(R.id.option2);
        RadioButton option3 = findViewById(R.id.option3);
        RadioButton option4 = findViewById(R.id.option4);
        RadioButton option5 = findViewById(R.id.option5);
        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;

                //Updating the total emissions based on user response
                if (occasionally.isChecked()) {

                    if (option1.isChecked()) {
                        emissions2 += 246;
                    } else if (option2.isChecked()) {
                        emissions2 += 819;
                    } else if (option3.isChecked()) {
                        emissions2 += 1638;
                    } else if (option4.isChecked()) {
                        emissions2 += 3071;
                    } else if (option5.isChecked()) {
                        emissions2 += 4095;
                    }

                } else if (frequently.isChecked()) {

                    if (option1.isChecked()) {
                        emissions2 += 573;
                    } else if (option2.isChecked()) {
                        emissions2 += 1911;
                    } else if (option3.isChecked()) {
                        emissions2 += 3822;
                    } else if (option4.isChecked()) {
                        emissions2 += 7166;
                    } else if (option5.isChecked()) {
                        emissions2 += 9555;
                    }

                } else if (always.isChecked()) {

                    if (option1.isChecked()) {
                        emissions2 += 1050;
                    } else if (option2.isChecked()) {
                        emissions2 += 2363;
                    } else if (option3.isChecked()) {
                        emissions2 += 4103;
                    } else if (option4.isChecked()) {
                        emissions2 += 9611;
                    } else if (option5.isChecked()) {
                        emissions2 += 13750;
                    }

                }

                //Checking if user responded
                if (emissions2 != emissions || never.isChecked()) {

                    //Storing the amount of emissions that come from transit
                    double transit_emissions = emissions2 - emissions;

                    //Moving to next set of questions about flights
                    Intent intent = new Intent(QuestionnaireTransitActivity.this, QuestionnaireFlightActivity.class);
                    intent.putExtra("current_emissions", emissions2);
                    intent.putExtra("car_emissions", car_emissions);
                    intent.putExtra("transit_emissions", transit_emissions);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}