package com.example.b07projectfall2024.LoginPage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.ForgotPasswordPage.ForgotPasswordView;
import com.example.b07projectfall2024.R;
import com.example.b07projectfall2024.RegisterPage.RegisterActivityView;

import java.util.HashMap;

public class LoginActivityView extends AppCompatActivity {

    private HashMap<String, EditText> InputFields;
    private LoginActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        presenter = new LoginActivityPresenter(this, new LoginActivityModel());

        InputFields = new HashMap<String, EditText>();

        Button buttonLogin = findViewById(R.id.loginButton);
        Button registerRedirect = findViewById(R.id.Login_registerRedirect);
        Button ForgotPassword = findViewById(R.id.Login_forgotPasswordButton);

        InputFields.put("EmailField", (EditText)findViewById(R.id.loginEmailField));
        InputFields.put("PasswordField", (EditText)findViewById(R.id.loginPasswordField));

        //Redirect user back to Register page
        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(RegisterActivityView.class);
            }
        });

        /*
        On click attempt to login the user in with email and password fields
         */
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(InputFields.containsKey("PasswordField")&& InputFields.containsKey("EmailField"))) return;

                String email = InputFields.get("EmailField").getText().toString();
                String password = InputFields.get("PasswordField").getText().toString();
                presenter.Login(email, password);
            }
        });

        //Redirect user to forgot password page
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(ForgotPasswordView.class);
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

    /*
     Show a dialog box for general errors (e.g., invalid email and/or password)
     */
    public void ShowPopupMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
