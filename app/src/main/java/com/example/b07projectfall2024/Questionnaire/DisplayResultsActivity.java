package com.example.b07projectfall2024.Questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * DisplayResultsActivity
 * This activity is responsible for displaying the user's total carbon emissions from the past year,
 * with a breakdown of how much came from various factors, all obtained through the questionnaire
 * activities. It navigates to MainActivity
 */
public class DisplayResultsActivity extends AppCompatActivity {
    double kg_to_tons = 0.00110231; //kg to tons conversion rate

    // Firebase database reference
    private DatabaseReference dbReference;

    // UI components
    private TextView compareNationalText; // TextView to display comparison with national averages
    private Spinner countrySpinner; // Dropdown menu for country selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_results);

        // Initialize Firebase reference
        dbReference = FirebaseDatabase.getInstance().getReference();

        // Link UI components to their respective IDs in the XML layout
        countrySpinner = findViewById(R.id.country_spinner);
        compareNationalText = findViewById(R.id.compare_national);

        Intent intent = getIntent();
        double final_emissions = intent.getDoubleExtra("final_emissions", 0.0);
        double consumption_emissions = intent.getDoubleExtra("consumption_emissions", 0.0);
        double housing_emissions = intent.getDoubleExtra("housing_emissions", 0.0);
        double diet_emissions = intent.getDoubleExtra("diet_emissions", 0.0);
        double car_emissions = intent.getDoubleExtra("car_emissions", 0.0);
        double transit_emissions = intent.getDoubleExtra("transit_emissions", 0.0);
        double flight_emissions = intent.getDoubleExtra("flight_emissions", 0.0);

        //Total emissions
        TextView total_emissions_text = findViewById(R.id.total_emissions_text);

        //Emissions my factor
        TextView car_emissions_text = findViewById(R.id.car_emissions);
        TextView transit_emissions_text = findViewById(R.id.transit_emissions);
        TextView flight_emissions_text = findViewById(R.id.flight_emissions);
        TextView diet_emissions_text = findViewById(R.id.diet_emissions);
        TextView housing_emissions_text = findViewById(R.id.housing_emissions);
        TextView consumption_emissions_text = findViewById(R.id.consumption_emissions);
      
        TextView compare_national_text = findViewById(R.id.compare_national); // Add a new TextView in XML

        //Comparison to global standards
        TextView compare_global_text = findViewById(R.id.compare_global);

        //next button
        Button next = findViewById(R.id.next);

        //Displaying the user's total carbon emissions from the past year
        total_emissions_text.setText(roundThreeDec(final_emissions * kg_to_tons) + " tons");

        //Displaying the breakdown of the user's carbon emissions by factor
        TextView[] emissionsDisplays = {car_emissions_text, transit_emissions_text, flight_emissions_text,
                diet_emissions_text, housing_emissions_text, consumption_emissions_text};
        double[] emissions = {car_emissions, transit_emissions, flight_emissions, diet_emissions,
                housing_emissions, consumption_emissions};
        String[] factors = {"Car", "Transit", "Flight", "Diet", "Housing", "Consumption"};
        displayBreakdown(emissions, emissionsDisplays, factors);

        //Comparing the user's carbon emissions to global targets (2tons/CO2 per year)
        compareGlobal(compare_global_text, final_emissions, 2);

        // Set up Spinner for country selection
        setupCountrySpinner(final_emissions * kg_to_tons);

        // Storing their emissions in the database and moving to main activity.
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                Map<String, Double> m = new HashMap<>();
                m.put("total_emissions", final_emissions * kg_to_tons);
                m.put("car_emissions", car_emissions * kg_to_tons);
                m.put("transit_emissions", transit_emissions * kg_to_tons);
                m.put("flight_emissions", flight_emissions * kg_to_tons);
                m.put("diet_emissions", diet_emissions * kg_to_tons);
                m.put("housing_emissions", housing_emissions * kg_to_tons);
                m.put("consumption_emissions", consumption_emissions * kg_to_tons);

                db.child("users").child(user.getUid())
                        .child("questionnaire_emissions").setValue(m).addOnSuccessListener(

                        documentReference -> {

                            //Navigating to Main Activity
                            Intent intent2 = new Intent(DisplayResultsActivity.this,
                                    MainActivity.class);
                            startActivity(intent2);
                            finish();

                        }
                );

            }
        });
    }


    /**
     * Rounds a decimal value to 3 decimal places
     * @param toRound The 'double' value to round
     * @return toRound rounded to 3 decimal places as a 'double'
     */
    private double roundThreeDec(double toRound) {
        return (double)Math.round(toRound * 1000) / 1000;
    }

    /**
     * Displays a breakdown of emissions to various TextViews in tons, with each statement
     * working with a the same index from each of the 3 input arrays
     * @param emissions The array of emissions values to display. Must be in kg.
     * @param emissionDisplays The array of TextViews to which emissions are displayed
     * @param factors The array of Strings that each emission corresponds to
     */
    private void displayBreakdown(double[] emissions, TextView[] emissionDisplays, String[] factors) {
        for (int i = 0; i < emissionDisplays.length; i++) {
            emissionDisplays[i].setText(factors[i] + " Emissions: "
                    + roundThreeDec(emissions[i] * kg_to_tons) + " tons");
        }

    }

    /**
     * Displays a comparison of an emission value and corresponding global standard
     * @param view The TextView to which the comparison is displayed
     * @param finalEmissions The emission value to compare (must be in kg)
     * @param globalStandard The global standard to compare to (must be in tons)
     */
    private void compareGlobal(TextView view, double finalEmissions, double globalStandard) {
        double diff = roundThreeDec(globalStandard - (finalEmissions * kg_to_tons));
        boolean under = diff > 0;
        String overUnder = "above";
        if (under) {
            overUnder = "below";
        }
        view.setText("Your emissions are " + Math.abs(diff) + " tons " + overUnder
                + " global targets to reduce climate change!");

      
    // Set up spinner for country selection and comparison
    private void setupCountrySpinner(double userEmissionsInTons) {

        // Bind spinner to a string array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Handle country selection
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                if (!selectedCountry.equals("Select a country")) { // Avoid default
                    fetchCountryEmissions(selectedCountry, userEmissionsInTons);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                compareNationalText.setText("Please select a country.");
            }
        });
    }

    // Fetch emissions data for the selected country from Firebase
    private void fetchCountryEmissions(String country, double userEmissionsInTons) {
        dbReference.child("Countries").child(country).child("total_emissions")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getValue() != null) {

                            // Fetch national average emissions in kilograms and convert to tons
                            double nationalAverageInKg = snapshot.getValue(Double.class);
                            double nationalAverageInTons = nationalAverageInKg * 0.00110231;

                            // Calculate difference between user and country emissions in tons
                            double nationalDifference = userEmissionsInTons - nationalAverageInTons;
                            String comparison = nationalDifference > 0 ? "above" : "below";

                            // Display the comparison in tons
                            compareNationalText.setText(String.format(Locale.getDefault(),
                                    "Your emissions are %.2f tons %s the national average emissions in %s (%.2f tons)!",
                                    Math.abs(nationalDifference), comparison, country, nationalAverageInTons));
                        }

                        else {
                            // Handle missing data
                            compareNationalText.setText(String.format("No data available for %s.", country));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Handle Firebase errors
                        compareNationalText.setText("Failed to fetch data. Please try again.");
                    }
                });
    }

}
