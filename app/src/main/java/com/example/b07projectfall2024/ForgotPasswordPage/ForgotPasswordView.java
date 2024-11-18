package com.example.b07projectfall2024.ForgotPasswordPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.example.b07projectfall2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordView extends AppCompatActivity {
    private EditText EmailField;
    private ForgotPasswordPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        presenter = new ForgotPasswordPresenter(new ForgotPasswordModel(), this);

        Button ReturnToLogin = findViewById(R.id.ForgotPassword_BackLoginButton);
        Button SubmitEmail = findViewById(R.id.ForgotPassword_ResetEmailButton);

        EmailField = findViewById(R.id.ForgotPassword_ResetEmailField);

        //Redirect user to Login page
        ReturnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.PageRedirect(LoginActivityView.class);
            }
        });

        //Attempt to send a reset password email
        SubmitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailField.getText().toString().trim();
                presenter.CheckSendEmail(email);
            }
        });
    }

    //Set error in EmailField with message ErrorMsg and request focus onto the field
    public void EmailFieldError(String ErrorMsg){
        EmailField.setError(ErrorMsg);
        EmailField.requestFocus();
    }
}
