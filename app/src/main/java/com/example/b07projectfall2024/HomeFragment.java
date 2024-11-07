package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class HomeFragment extends FragmentTemplate {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            loadFragment(new LoginFragment());
        }else{
            Button buttonRecyclerView = view.findViewById(R.id.buttonRecyclerView);
            Button buttonScroller = view.findViewById(R.id.buttonScroller);
            Button buttonSpinner = view.findViewById(R.id.buttonSpinner);
            Button buttonManageItems = view.findViewById(R.id.buttonManageItems);

            buttonRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new RecyclerViewFragment());
                }
            });

            buttonScroller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new ScrollerFragment());
                }
            });

            buttonSpinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new SpinnerFragment());
                }
            });

            buttonManageItems.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { loadFragment(new ManageItemsFragment());}
            });
        }
        return view;
    }
}
