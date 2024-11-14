package com.example.b07projectfall2024.ForgotPasswordPage;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordPresenter {
    private ForgotPasswordModel model;
    private ForgotPasswordView view;

    public ForgotPasswordPresenter(ForgotPasswordModel model, ForgotPasswordView view){
        this.view= view;
        this.model = model;
    }

    public void CheckSendEmail(String email){
        if (email.isEmpty()) {
            view.EmailFieldError("Email is required");
            return;
        }
        model.sendEmail(this, email);
    }

    public void MakeToast(String Msg){
        Toast.makeText(view, Msg, Toast.LENGTH_SHORT).show();
    }

    public <T extends AppCompatActivity> void PageRedirect(Class<T> TargetPage) {
        view.startActivity(new Intent(view, TargetPage));
    }
}
