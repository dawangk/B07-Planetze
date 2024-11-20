package com.example.b07projectfall2024.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.R;

public class DisplayResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_results);

        Intent intent = getIntent();
        double final_emissions = intent.getDoubleExtra("final_emissions", 0.0);
        double consumption_emissions = intent.getDoubleExtra("consumption_emissions", 0.0);
        double housing_emissions = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        TextView total_emissions_text = findViewById(R.id.total_emissions_text);
        TextView car_emissions_text = findViewById(R.id.car_emissions);
        TextView transit_emissions_text = findViewById(R.id.transit_emissions);
        TextView flight_emissions_text = findViewById(R.id.flight_emissions);
        TextView diet_emissions_text = findViewById(R.id.diet_emissions);
        TextView housing_emissions_text = findViewById(R.id.housing_emissions);
        TextView consumption_emissions_text = findViewById(R.id.consumption_emissions);
        Button next = findViewById(R.id.next);

        //Displaying the user's total carbon emissions
        total_emissions_text.setText((final_emissions * 0.00110231) + " tons");

        //Displaying the breakdown of the user's carbon emissions
        car_emissions_text.setText("Car Emissions: " + (car_emissions * 0.00110231) + " tons");
        transit_emissions_text.setText("Transit Emissions: " + (transit_emissions * 0.00110231) + " tons");
        flight_emissions_text.setText("Flight Emissions: " + (flight_emissions * 0.00110231) + "tons");
        diet_emissions_text.setText("Diet Emissions: " + (diet_emissions * 0.00110231)+ " tons");
        housing_emissions_text.setText("Housing Emissions: " + (housing_emissions * 0.00110231) + " tons");
        consumption_emissions_text.setText("Consumption Emissions: " + (consumption_emissions * 0.00110231) + " tons");

        }
}
