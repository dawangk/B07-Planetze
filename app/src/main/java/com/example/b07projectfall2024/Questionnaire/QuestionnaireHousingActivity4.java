package com.example.b07projectfall2024.Questionnaire;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.RadioButton;
import com.example.b07projectfall2024.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuestionnaireHousingActivity4 extends AppCompatActivity {

    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_questionnaire_housing4);

        Intent intent = getIntent();
        double emissions = intent.getDoubleExtra("current_emissions", 0.0);
        double housing_emissions_rough = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);
        String house_type = intent.getStringExtra("house_type");
        int num_people = intent.getIntExtra("num_people", 0);
        String house_size = intent.getStringExtra("house_size");
        String energy_type = intent.getStringExtra("energy_type");
        String energy_type2 = intent.getStringExtra("energy_type2");
        int monthly_bill = intent.getIntExtra("monthly_bill", 0);

        RadioButton primarily = findViewById(R.id.primarily);
        RadioButton partially = findViewById(R.id.partially);
        RadioButton no = findViewById(R.id.no);

        next = findViewById(R.id.next);

        //Fetching the emissions corresponding to the house type mentioned by user.
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Housing Emissions/" + house_type + "/"
                + house_size + "/" + num_people + "/" + energy_type + "/" + monthly_bill);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking if user responded
                if (primarily.isChecked() || partially.isChecked()|| no.isChecked()) {

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {

                                double housing_emissions = snapshot.getValue(double.class);

                                //Updating total emissions based on user response
                                if (primarily.isChecked()) {
                                    housing_emissions -= 6000;
                                } else if (partially.isChecked()) {
                                    housing_emissions -= 4000;
                                }

                                if (!(energy_type.equals(energy_type2))) {

                                    housing_emissions += 233;
                                }

                                double total_emissions = emissions + housing_emissions;

                                //Moving to next set of questions about consumption
                                Intent intent = new Intent(QuestionnaireHousingActivity4.this, QuestionnaireConsumptionActivity.class);
                                intent.putExtra("current_emissions", total_emissions);
                                intent.putExtra("diet_emissions", diet_emissions);
                                intent.putExtra("car_emissions", car_emissions);
                                intent.putExtra("transit_emissions", transit_emissions);
                                intent.putExtra("flight_emissions", flight_emissions);
                                intent.putExtra("housing_emissions", housing_emissions);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            System.out.println("Error");
                        }
                    });

                }
            }
        });
    }
}