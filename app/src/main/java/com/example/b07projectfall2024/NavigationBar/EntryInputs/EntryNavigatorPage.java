package com.example.b07projectfall2024.NavigationBar.EntryInputs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.b07projectfall2024.HomeActivity;
import com.example.b07projectfall2024.R;

public class EntryNavigatorPage extends Fragment {

    private HomeActivity home;
    public EntryNavigatorPage(HomeActivity home){
        this.home = home;
    }

    public EntryNavigatorPage(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_navigator, container, false);

        Button buttonTransport = view.findViewById(R.id.EntryNavigator_Transportation);
        Button buttonFood = view.findViewById(R.id.EntryNavigator_Food);
        Button buttonShopping = view.findViewById(R.id.EntryNavigator_Shopping);

        buttonTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.loadFragment(new TransportEntryPage(), true);
            }
        });

        buttonFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.loadFragment(new FoodEntryPage(), true);
            }
        });

        buttonShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home.loadFragment(new ConsumptionEntry(), true);
            }
        });

        return view;
    }

}
