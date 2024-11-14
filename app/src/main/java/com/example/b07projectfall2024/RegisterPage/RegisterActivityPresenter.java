package com.example.b07projectfall2024.RegisterPage;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivityPresenter {

    private RegisterActivityModel model;
    private RegisterActivityView view;

    public RegisterActivityPresenter(RegisterActivityView view, RegisterActivityModel model){
        this.model = model;
        this.view = view;
    }

    public void registerUser(String email, String password, String verifypassword, String name) {

        if (email.isEmpty()) {
            view.SetErrorField("EmailField", "Email is required");
            return;
        }

        if (password.isEmpty()) {
            view.SetErrorField("PasswordField", "Password is required");
            return;
        }else if(password.length()<6) {
            view.SetErrorField("PasswordField", "Password must be at least 6 characters in length");
            return;
        }

        if(name.isEmpty()){
            view.SetErrorField("NameField", "Name is required");
            return;
        }

        if(verifypassword.isEmpty()){
            view.SetErrorField("VerifyPasswordField", "Please verify your password");
            return;
        } else if(!verifypassword.equals(password)){
            view.SetErrorField("VerifyPasswordField", "Passwords do not match");
            return;
        }
        model.RegisterUser(this, email, password, name);

    }

    public <T extends AppCompatActivity> void PageRedirect(Class<T> TargetPage) {
        view.startActivity(new Intent(view, TargetPage));
    }

}
