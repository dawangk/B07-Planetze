package com.example.b07projectfall2024.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

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
        TextView compare_global_text = findViewById(R.id.compare_global);
        Button next = findViewById(R.id.next);

        double kg_to_tons = 0.00110231;
        //Displaying the user's total carbon emissions
        total_emissions_text.setText((final_emissions * kg_to_tons) + " tons");

        //Displaying the breakdown of the user's carbon emissions
        car_emissions_text.setText("Car Emissions: " + (double)Math.round((car_emissions * kg_to_tons) * 1000) / 1000 + " tons");
        transit_emissions_text.setText("Transit Emissions: " + (double)Math.round((transit_emissions * kg_to_tons) * 1000) / 1000 + " tons");
        flight_emissions_text.setText("Flight Emissions: " + (double)Math.round((flight_emissions * kg_to_tons) * 1000) / 1000 + "tons");
        diet_emissions_text.setText("Diet Emissions: " + (double)Math.round((diet_emissions * kg_to_tons) * 1000) / 1000 + " tons");
        housing_emissions_text.setText("Housing Emissions: " + (double)Math.round((housing_emissions * kg_to_tons) * 1000) / 1000 + " tons");
        consumption_emissions_text.setText("Consumption Emissions: " + (double)Math.round((consumption_emissions * kg_to_tons) * 1000) / 1000 + " tons");

        //Comparing the user's carbon emissions to global targets (2tons/CO2 per year)
        double difference = (double)Math.round((2 - (final_emissions * kg_to_tons)) * 1000) / 1000;
        boolean under = difference > 0;
        String overUnder = "above";
        if (under) {
            overUnder = "below";
        }
        compare_global_text.setText("Your emissions are " + Math.abs(difference) + " tons " + overUnder + " global targets to reduce climate change!");


        //Storing their emissions in the database and moving to main activity.
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                Map<String, Double> m = new HashMap<String, Double>();
                m.put("total_emissions", final_emissions * kg_to_tons);
                m.put("car_emissions", car_emissions * kg_to_tons);
                m.put("transit_emissions", transit_emissions * kg_to_tons);
                m.put("flight_emissions", flight_emissions * kg_to_tons);
                m.put("diet_emissions", diet_emissions * kg_to_tons);
                m.put("housing_emissions", housing_emissions * kg_to_tons);
                m.put("consumption_emissions", consumption_emissions * kg_to_tons);

                db.child("users").child(user.getUid()).child("questionnaire_emissions").setValue(m).addOnSuccessListener(
                        documentReference -> {

                            Intent intent2 = new Intent(DisplayResultsActivity.this, MainActivity.class);
                            startActivity(intent2);
                            finish();

                        }
                );

            }
        });
    }
}
