package com.example.b07projectfall2024.LoginPage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(RegisterActivityView.class);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(InputFields.containsKey("PasswordField")&& InputFields.containsKey("EmailField"))) return;

                String email = InputFields.get("EmailField").getText().toString();
                String password = InputFields.get("PasswordField").getText().toString();
                presenter.Login(email, password);
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(ForgotPasswordView.class);
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
