package com.example.b07projectfall2024.NavigationBar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.b07projectfall2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.content.Context;


public class DashboardFragment extends Fragment {

    Context currentContext;
    String CurrentSelectedDate;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_dashboard, container, false); }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        currentContext = getContext();

        Button dateUpdate = rootView.findViewById(R.id.dateUpdate);

        TextView dateTextView = rootView.findViewById(R.id.date);
        DateFieldInit(dateTextView);

        TextView total_emissions = rootView.findViewById(R.id.total_emissions_text);
        TextView transport_emissions = rootView.findViewById(R.id.transport_emissions);
        TextView diet_emissions = rootView.findViewById(R.id.diet_emissions);
        TextView consumption_emissions = rootView.findViewById(R.id.consumption_emissions);

        //FOR TODAY
        DatabaseReference dayRef = ref.child("users").child(user.getUid()).child("entries").child(CurrentSelectedDate);

        //If entries for the day exist, display. Else, display zero.
        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double[] emissions = getDayEmissions(dayRef);
                    total_emissions.setText((emissions[0] + emissions[1] + emissions[2]) + "");
                    transport_emissions.setText("Transport Emissions: " + emissions[0] + "kg");
                    diet_emissions.setText("Diet Emissions: " + emissions[1] + "kg");
                    consumption_emissions.setText("Consumption Emissions: " + emissions[2] + "kg");
                }

                else {
                    total_emissions.setText(0 + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //When selected date is updated, do the same as above
        dateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference dayRef2 = ref.child("users").child(user.getUid()).child("entries").child(CurrentSelectedDate);

                //If entries for the day exist, display. Else, display zero.
                dayRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            double[] emissions = getDayEmissions(dayRef2);
                            total_emissions.setText((emissions[0] + emissions[1] + emissions[2]) + "");
                            transport_emissions.setText("Transport Emissions: " + emissions[0] + "kg");
                            diet_emissions.setText("Diet Emissions: " + emissions[1] + "kg");
                            consumption_emissions.setText("Consumption Emissions: " + emissions[2] + "kg");
                        }

                        else {
                            total_emissions.setText(0 + "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });
    }

    //Initializes DateField allowing users to select any date
    private void DateFieldInit(TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());

        dateTextView.setText(todayDate);
        CurrentSelectedDate = todayDate;

        dateTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    currentContext,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        dateTextView.setText(selectedDate);
                        CurrentSelectedDate = selectedDate;
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    //Gets the total day emissions given a day DatabaseReference.
    private double[] getDayEmissions(DatabaseReference dayRef) {

        double[] transportEmissions = {0};
        double[] foodEmissions = {0};
        double[] consumptionEmissions = {0};

        //Creating DatabaseReferences
        DatabaseReference transportEntries = dayRef.child("transportation");
        DatabaseReference foodEntries = dayRef.child("food");
        DatabaseReference consumptionEntries = dayRef.child("consumption");

        //Checking if such entry types exist and updating their emissions accordingly
        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    transportEmissions[0] = getTransportEmissions(transportEntries);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    foodEmissions[0] = getFoodEmissions(foodEntries);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    consumptionEmissions[0] = getConsumptionEmissions(consumptionEntries);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return new double[] {transportEmissions[0], foodEmissions[0], consumptionEmissions[0]};
    }

    private double getTransportEmissions(DatabaseReference transportEntries) {

        double[] transportEmissions = {0};

        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DatabaseReference transportTypeRef = childSnapshot.getRef().child("TransportationType");
                    String[] transportType = {""};
                    transportTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                transportType[0] = snapshot.getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    //Different calculations depending on transportation type
                    switch (transportType[0]) {

                        case "Car":

                            DatabaseReference carTypeRef = childSnapshot.getRef().child("CarType");
                            String[] carType = {""};
                            carTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        carType[0] = snapshot.getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Car Emission Rates").child(carType[0]);
                            double[] rate = {0};

                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Double.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            DatabaseReference distanceDrivenRef = childSnapshot.getRef().child("Distance");
                            double[] distanceDriven = {0.0};
                            distanceDrivenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        distanceDriven[0] = snapshot.getValue(Double.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            transportEmissions[0] += rate[0] * distanceDriven[0];
                            break;

                        case "Public":

                            DatabaseReference timeOnPublicRef = childSnapshot.getRef().child("TimeOnPublic");
                            double[] timeOnPublic = {0.0};
                            timeOnPublicRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        timeOnPublic[0] = snapshot.getValue(Double.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            transportEmissions[0] += timeOnPublic[0] * 150;
                            break;

                        case "Flight":

                            DatabaseReference numFlightsRef = childSnapshot.getRef().child("NmbFlights");
                            int[] numFlights = {0};
                            numFlightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        numFlights[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            DatabaseReference flightTypeRef = childSnapshot.getRef().child("FlightType");
                            String[] flightType = {""};
                            flightTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        flightType[0] = snapshot.getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            double rate2;

                            if (flightType[0].equals("Short-haul (<1,500 km)")) {
                                rate2 = 150;
                            }
                            else {
                                rate2 = 550;
                            }

                            transportEmissions[0] += rate2 * numFlights[0];

                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return transportEmissions[0];
    }

    private double getFoodEmissions(DatabaseReference foodEntries) {

        double[] foodEmissions = {0};

        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DatabaseReference mealTypeRef = childSnapshot.getRef().child("MealType");
                    String[] mealType = {""};
                    mealTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                mealType[0] = snapshot.getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Food Emissions Rate").child(mealType[0]);
                    double[] rate = {0};
                    rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                rate[0] = snapshot.getValue(Double.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    DatabaseReference numServingsRef = childSnapshot.getRef().child("NmbConsumedServings");
                    int[] numServings = {0};
                    numServingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                numServings[0] = snapshot.getValue(Integer.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    foodEmissions[0] += rate[0] * numServings[0];
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return foodEmissions[0];
    }

    private double getConsumptionEmissions(DatabaseReference consumptionEntries) {

        double[] consumptionEmissions = {0};

        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DatabaseReference boughtItemRef = childSnapshot.getRef().child("BoughtItem");
                    String[] boughtItem = {""};
                    boughtItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                boughtItem[0] = snapshot.getValue(String.class);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    switch (boughtItem[0]) {

                        case "Clothes":

                            DatabaseReference numClothesRef = childSnapshot.getRef().child("NmbClothingBought");
                            int[] numClothes = {0};
                            numClothesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        numClothes[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            consumptionEmissions[0] += 10 * numClothes[0];
                            break;

                        case "Electronics":

                            DatabaseReference numPurchasedRef = childSnapshot.getRef().child("NmbPurchased");
                            int[] numPurchased = {0};
                            numPurchasedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        numPurchased[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            DatabaseReference electronicTypeRef = childSnapshot.getRef().child("ElectronicType");
                            String[] electronicType = {""};
                            electronicTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        electronicType[0] = snapshot.getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            DatabaseReference rateRef = FirebaseDatabase.getInstance().getReference().child("Electronic Emission Rates").child(electronicType[0]);
                            int[] rate = {0};
                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            consumptionEmissions[0] += rate[0] * numPurchased[0];
                            break;

                        case "Utility Bill":

                            DatabaseReference billPriceRef = childSnapshot.getRef().child("BillPrice");
                            int[] billPrice = {0};
                            billPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        billPrice[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            DatabaseReference UtilityTypeRef = childSnapshot.getRef().child("UtilityType");
                            String[] UtilityType = {""};
                            UtilityTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        UtilityType[0] = snapshot.getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            DatabaseReference rateRef2 = FirebaseDatabase.getInstance().getReference().child("Utility Emission Rates").child(UtilityType[0]);
                            double[] rate2 = {0};
                            rateRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate2[0] = snapshot.getValue(Double.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            consumptionEmissions[0] += rate2[0] * billPrice[0];
                            break;

                        case "Other":

                            DatabaseReference numBoughtRef = childSnapshot.getRef().child("NmbPurchased");
                            int[] numBought = {0};
                            numBoughtRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        numBought[0] = snapshot.getValue(Integer.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            DatabaseReference itemTypeRef = childSnapshot.getRef().child("ItemType");
                            String[] itemType = {""};
                            itemTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        itemType[0] = snapshot.getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            int rate3 = 1;
                            if (itemType[0].equals("Furniture")) {
                                rate3 = 100;
                            } else if (itemType[0].equals("Appliances")) {
                                rate3 = 400;
                            }

                            consumptionEmissions[0] += rate3 * numBought[0];
                            break;

                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return consumptionEmissions[0];
    }
}

