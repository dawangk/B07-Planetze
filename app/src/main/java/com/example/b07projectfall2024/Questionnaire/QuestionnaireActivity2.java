package com.example.b07projectfall2024.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.example.b07projectfall2024.R;

/**
 * QuestionnaireActivity2
 * This activity is the second pertaining to the registration questionnaire.
 * It asks users if they drive a vehicle, and navigates to the corresponding activity
 */
public class QuestionnaireActivity2 extends AppCompatActivity {

    RadioButton yes;
    RadioButton no;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire2);

        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        next = findViewById(R.id.next);

        //When next is clicked, navigate to QuestionnaireCarActivity or QuestionnaireTransitActivity
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If user has a vehicle, we ask the next set of questions about their vehicle use.
                if (yes.isChecked()) {

                    Intent intent = new Intent(QuestionnaireActivity2.this, QuestionnaireCarActivity.class);
                    startActivity(intent);
                    finish();
                }

                //If they don't have a vehicle, we skip to questions regarding transit.
                else if (no.isChecked()) {
                    Intent intent = new Intent(QuestionnaireActivity2.this, QuestionnaireTransitActivity.class);
                    //By default, their emissions by car become 0.0 tons/CO2
                    intent.putExtra("current_emissions", 0.0);
                    intent.putExtra("car_emissions", 0.0);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}
