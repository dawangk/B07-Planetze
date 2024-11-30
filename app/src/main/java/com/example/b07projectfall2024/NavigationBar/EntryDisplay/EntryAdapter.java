package com.example.b07projectfall2024.NavigationBar.EntryDisplay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07projectfall2024.R;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private LinkedList< HashMap<String, Object>> entries; // Map of entry types and their details

    public EntryAdapter(LinkedList< HashMap<String, Object>> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_detail_item, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        //String detailType = (String) entries.keySet().toArray()[position]; // Transportation, Food, etc.
        HashMap<String, Object> details = entries.get(position);

        String detailType = (String) details.get("EntryCategory");
        holder.detailType.setText(detailType);

        // Dynamically populate `detailSpecificContainer` based on the detail type
        holder.detailSpecificContainer.removeAllViews(); // Clear previous views
        if (detailType.equals("transportation")) {
            populateTransportationDetails(holder.detailSpecificContainer, details);
        } else if (detailType.equals("food")) {
            populateFoodDetails(holder.detailSpecificContainer, details);
        } else if (detailType.equals("consumption")) {
            populateConsumptionDetails(holder.detailSpecificContainer, details);
        }

    }

    private void populateTransportationDetails(LinearLayout container, HashMap<String, Object> entry) {
        String transportationType = (String) entry.get("TransportationType");

        TextView textView = new TextView(container.getContext());
        textView.setText("Type: " + transportationType);
        textView.setPadding(0, 4, 0, 4);
        container.addView(textView);

        // Add specific details for transportation types
        if (transportationType.equals("Car")) {
            TextView carDetails = new TextView(container.getContext());
            carDetails.setText("Car Type: " + entry.get("CarType") + ", Distance: " + entry.get("Distance") + " km");
            container.addView(carDetails);
        } else if (transportationType.equals("Public")) {
            TextView publicDetails = new TextView(container.getContext());
            publicDetails.setText("Public Type: " + entry.get("PublicType") + ", Time: " + entry.get("TimeOnPublic") + " hours");
            container.addView(publicDetails);
        }else if (transportationType.equals("Plane")) {
            TextView publicDetails = new TextView(container.getContext());
            publicDetails.setText("Flight Type: " + entry.get("FlightType") + ", Number of Flights: " + entry.get("NumFlights") + " hours");
            container.addView(publicDetails);
        }

    }

    private void populateFoodDetails(LinearLayout container, HashMap<String, Object> entry) {

        TextView textView = new TextView(container.getContext());
        textView.setText("Meal Type: " + entry.get("MealType") + ", Servings: " + entry.get("NmbConsumedServings"));
        container.addView(textView);

    }

    private void populateConsumptionDetails(LinearLayout container, HashMap<String, Object> entry) {
        String boughtItem = (String) entry.get("BoughtItem");

        TextView textView = new TextView(container.getContext());
        textView.setText("Bought: " + boughtItem);
        textView.setPadding(0, 4, 0, 4);
        container.addView(textView);

        // Add specific details for bought items
        if (boughtItem.equals("Clothes")) {
            TextView clothingDetails = new TextView(container.getContext());
            clothingDetails.setText("Number: " + entry.get("NmbClothingBought") + ", Eco-Friendly: " + entry.get("EcoFriendly"));
            container.addView(clothingDetails);
        } else if (boughtItem.equals("Electronics")) {
            TextView electronicDetails = new TextView(container.getContext());
            electronicDetails.setText("Type: " + entry.get("ElectronicType") + ", Number: " + entry.get("NmbPurchased"));
            container.addView(electronicDetails);
        } else if (boughtItem.equals("Utility Bill")) {
            TextView electronicDetails = new TextView(container.getContext());
            electronicDetails.setText("Utility Type: " + entry.get("BillPrice") + ", Bill Price: " + entry.get("BillPrice"));
            container.addView(electronicDetails);
        } else if (boughtItem.equals("Other")) {
            TextView electronicDetails = new TextView(container.getContext());
            electronicDetails.setText("Item Type: " + entry.get("ItemType") + ", Number: " + entry.get("NmbPurchased"));
            container.addView(electronicDetails);
        }

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView detailType;
        LinearLayout detailSpecificContainer;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            detailType = itemView.findViewById(R.id.detailType);
            detailSpecificContainer = itemView.findViewById(R.id.detailSpecificContainer);
        }
    }
}
