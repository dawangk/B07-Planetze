package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;;import com.example.b07projectfall2024.DisplayResultsActivity;
import com.example.b07projectfall2024.R;

public class QuestionnaireConsumptionActivity2 extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_consumption2);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double consumption_emissions_rough = intent.getDoubleExtra("consumption_emissions", 0.0);
        double housing_emissions = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        next = findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double emissions2 = emissions;
                //ADD IMPLEMENTATION BASED ON FORMULA*****
                //MAKE SURE TO UPDATE consumption_emissions*****
                double consumption_emissions = consumption_emissions_rough;

                //Moving to results page
                Intent intent = new Intent(QuestionnaireConsumptionActivity2.this, DisplayResultsActivity.class);
                intent.putExtra("final_emissions", emissions2);
                intent.putExtra("diet_emissions", diet_emissions);
                intent.putExtra("car_emissions", car_emissions);
                intent.putExtra("transit_emissions", transit_emissions);
                intent.putExtra("flight_emissions", flight_emissions);
                intent.putExtra("housing_emissions", housing_emissions);
                intent.putExtra("consumption_emissions", consumption_emissions);
                startActivity(intent);
                finish();
            }
        });
    }
}