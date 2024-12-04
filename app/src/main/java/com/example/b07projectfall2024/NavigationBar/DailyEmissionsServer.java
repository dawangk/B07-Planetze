package com.example.b07projectfall2024.NavigationBar;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DailyEmissionsServer {

    DailyEmissionGetterReceiver Receiver;

    public DailyEmissionsServer(DailyEmissionGetterReceiver Receiver){
        this.Receiver=Receiver;
    }


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference();

    /**
     * Updates the dashboard to display the total day emissions of the newly selected date.
     */
    public void UpdateData (String CurrentSelectedDate){
        DatabaseReference dayRef2 = ref.child("users").child(user.getUid()).
                child("entries").child(CurrentSelectedDate);

        Receiver.init();

        //If entries for the day exist, display. Else, display zero.
        dayRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Receiver.init();
                if (snapshot.exists()) {
                    DatabaseReference transportEntries2 = dayRef2.child("transportation");
                    DatabaseReference foodEntries2 = dayRef2.child("food");
                    DatabaseReference consumptionEntries2 = dayRef2.child("consumption");
                    getTransportEmissions(transportEntries2, CurrentSelectedDate);
                    getFoodEmissions(foodEntries2, CurrentSelectedDate);
                    getConsumptionEmissions(consumptionEntries2, CurrentSelectedDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void calculateEmissionsForRange(String startDate, String endDate) {
        Receiver.init();


        ref.child("users").child(user.getUid()).child("entries")
                .orderByKey().startAt(startDate).endAt(endDate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                            String Date = dateSnapshot.getKey();
                            DatabaseReference transportEntries = dateSnapshot.child("transportation").getRef();
                            DatabaseReference foodEntries = dateSnapshot.child("food").getRef();
                            DatabaseReference consumptionEntries = dateSnapshot.child("consumption").getRef();

                            getTransportEmissions(transportEntries, Date);
                            getFoodEmissions(foodEntries, Date);
                            getConsumptionEmissions(consumptionEntries, Date);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }



    /**
     * Displays the transportation emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param transportEntries The DatabaseReference where transportation entries of the user are
     *                        stored
     * @param Date The Date of the associated with the Entry
     */
    private void getTransportEmissions(DatabaseReference transportEntries, String Date) {
        transportEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DataSnapshot transportTypeRef = childSnapshot.child("TransportationType");
                    String transportType = transportTypeRef.getValue(String.class);

                    //Different calculations depending on transportation type
                    switch (transportType) {

                        case "Car":

                            DataSnapshot carTypeRef = childSnapshot.child("CarType");
                            String carType = carTypeRef.getValue(String.class);

                            //Getting the emission rate of the user's car based on carType
                            DatabaseReference rateRef = db.getReference()
                                    .child("Car Emission Rates").child(carType);
                            double[] rate = {0};

                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Double.class);
                                        DataSnapshot distanceDrivenRef = childSnapshot
                                                .child("Distance");
                                        double distanceDriven = distanceDrivenRef.getValue(Double.class);
                                        Receiver.TransportAction(rate[0] * distanceDriven, Date);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            break;

                        case "Public":

                            DataSnapshot timeOnPublicRef = childSnapshot.child("TimeOnPublic");
                            double timeOnPublic = timeOnPublicRef.getValue(Double.class);
                            Receiver.TransportAction(timeOnPublic * 150, Date);
                            break;

                        case "Plane":

                            DataSnapshot numFlightsRef = childSnapshot.child("NmbFlights");
                            int numFlights = numFlightsRef.getValue(Integer.class);

                            DataSnapshot flightTypeRef = childSnapshot.child("FlightType");
                            String flightType = flightTypeRef.getValue(String.class);

                            double rate2;

                            if (flightType.equals("Short-haul (<1,500 km)")) {
                                rate2 = 150;
                            } else {
                                rate2 = 550;
                            }
                            Receiver.TransportAction(rate2 * numFlights, Date);
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
    }

    /**
     * Displays the diet emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param foodEntries The DatabaseReference where food entries of the user are stored
     * @param Date The Date of the associated with the Entry
     */
    private void getFoodEmissions(DatabaseReference foodEntries, String Date) {
        foodEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all food entries
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                    DataSnapshot mealTypeRef = childSnapshot.child("MealType");
                    String mealType = mealTypeRef.getValue(String.class);

                    //Getting the emission rate depending on MealType
                    DatabaseReference rateRef = db.getReference().child("Food Emissions Rates").child(mealType);
                    double[] rate = {0};
                    rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                rate[0] = snapshot.getValue(Double.class);
                                DataSnapshot numServingsRef = childSnapshot.child("NmbConsumedServings");
                                int numServings = numServingsRef.getValue(Integer.class);
                                Receiver.FoodAction(rate[0] * numServings, Date);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Displays the consumption emissions of the currently selected date, and updates the total
     * emissions accordingly
     * @param consumptionEntries The DatabaseReference where consumption entries of the user are
     *                         stored
     * @param Date The Date of the associated with the Entry
     */
    private void getConsumptionEmissions(DatabaseReference consumptionEntries, String Date) {
        consumptionEntries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Looping over all transportation entries
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                    DataSnapshot boughtItemRef = childSnapshot.child("BoughtItem");
                    String boughtItem = boughtItemRef.getValue(String.class);

                    switch (boughtItem) {

                        case "Clothes":

                            DataSnapshot numClothesRef = childSnapshot.child("NmbClothingBought");
                            int numClothes = numClothesRef.getValue(Integer.class);

                            Receiver.ConsumptionAction(10 * numClothes, Date);

                            break;

                        case "Electronics":

                            DataSnapshot numPurchasedRef = childSnapshot.child("NmbPurchased");
                            int numPurchased = numPurchasedRef.getValue(Integer.class);

                            DataSnapshot electronicTypeRef = childSnapshot.child("ElectronicType");
                            String electronicType = electronicTypeRef.getValue(String.class);

                            //Getting the rate (price) based on electronicType
                            DatabaseReference rateRef = db.getReference().child("Electronic Emission Rates").child(electronicType);
                            int[] rate = {0};

                            rateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate[0] = snapshot.getValue(Integer.class);
                                        Receiver.ConsumptionAction(rate[0] * numPurchased, Date);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            break;

                        case "Utility Bill":

                            DataSnapshot billPriceRef = childSnapshot.child("BillPrice");
                            int billPrice = billPriceRef.getValue(Integer.class);

                            DataSnapshot UtilityTypeRef = childSnapshot.child("UtilityType");
                            String UtilityType = UtilityTypeRef.getValue(String.class);

                            //Getting rate based on UtilityType
                            DatabaseReference rateRef2 = db.getReference().child("Utility Emission Rates").child(UtilityType);
                            double[] rate2 = {0};
                            rateRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        rate2[0] = snapshot.getValue(Double.class);
                                        Receiver.ConsumptionAction(rate2[0] * billPrice, Date);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            break;

                        case "Other":

                            DataSnapshot numBoughtRef = childSnapshot.child("NmbPurchased");
                            int numBought = numBoughtRef.getValue(Integer.class);

                            DataSnapshot itemTypeRef = childSnapshot.child("ItemType");
                            String itemType = itemTypeRef.getValue(String.class);

                            int rate3 = 1;
                            if (itemType.equals("Furniture")) {
                                rate3 = 100;
                            } else if (itemType.equals("Appliances")) {
                                rate3 = 400;
                            }
                            Receiver.ConsumptionAction(rate3 * numBought, Date);
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
    }
}
