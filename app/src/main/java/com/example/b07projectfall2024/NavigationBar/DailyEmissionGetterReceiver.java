package com.example.b07projectfall2024.NavigationBar;

public interface DailyEmissionGetterReceiver {

    public void TransportAction(double AdditionalEmissions, String Date);
    public void FoodAction(double AdditionalEmissions, String Date);
    public void ConsumptionAction(double AdditionalEmissions, String Date);
    public void init();

}
