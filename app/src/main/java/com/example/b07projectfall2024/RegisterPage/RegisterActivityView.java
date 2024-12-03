package com.example.b07projectfall2024.RegisterPage;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.example.b07projectfall2024.R;
import java.util.HashMap;


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

        //redirect user to login page
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(LoginActivityView.class);
            }
        });

        //attempt to create a new user with the field params
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

    /*
    Given the FieldName and ErrorMsg
    If FieldName exists within this view then call setError on that given field with Error message ErrorMsg
    as well as requesting focus.
     */
    public void SetErrorField(String FieldName, String ErrorMsg){
        if(InputFields.containsKey(FieldName)){
            InputFields.get(FieldName).setError(ErrorMsg);
            InputFields.get(FieldName).requestFocus();
        }
    }




}
