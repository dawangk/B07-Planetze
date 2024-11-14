package com.example.b07projectfall2024.LoginPage;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivityPresenter {
    private LoginActivityView view;
    private LoginActivityModel model;
    public LoginActivityPresenter(LoginActivityView view, LoginActivityModel model){
        this.view = view;
        this.model = model;
    }

    public void Login(String email, String password){
        if (email.isEmpty()) {
            view.SetErrorField("EmailError", "Email is required");
            return;
        }

        if (password.isEmpty()) {
            view.SetErrorField("PasswordError", "Password is required");
            return;
        }else if (password.length()<6) {
            view.SetErrorField("PasswordError", "Password must be at least 6 characters in length");
            return;
        }
        model.LoginUser(this, email, password);
    }

    public <T extends AppCompatActivity> void PageRedirect(Class<T> TargetPage) {
        view.startActivity(new Intent(view, TargetPage));
    }
}
