package com.example.b07projectfall2024.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.example.b07projectfall2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivityView extends AppCompatActivity {


    private HashMap<String, EditText> InputFields;
    private RegisterActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        presenter = new RegisterActivityPresenter(this, new RegisterActivityModel());
        InputFields = new HashMap<String, EditText>();

        Button registerButton = findViewById(R.id.Register_registerButton);
        Button loginRedirect = findViewById(R.id.Register_redirectLoginButton);

        InputFields.put("EmailField", (EditText)findViewById(R.id.Register_emailField));
        InputFields.put("NameField", (EditText)findViewById(R.id.Register_nameField));
        InputFields.put("PasswordField", (EditText)findViewById(R.id.Register_passwordField));
        InputFields.put("VerifyPasswordField", (EditText)findViewById(R.id.Register_verifyPasswordField));

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(LoginActivityView.class);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = InputFields.get("NameField").getText().toString().trim();
                String email = InputFields.get("EmailField").getText().toString().trim();
                String password = InputFields.get("PasswordField").getText().toString().trim();
                String verifypassword = InputFields.get("VerifyPasswordField").getText().toString().trim();
                presenter.registerUser(email, password, verifypassword, name);
            }
        });
    }

    public void SetErrorField(String FieldName, String ErrorMsg){
        if(InputFields.containsKey(FieldName)){
            InputFields.get(FieldName).setError(ErrorMsg);
            InputFields.get(FieldName).requestFocus();
        }
    }




}
