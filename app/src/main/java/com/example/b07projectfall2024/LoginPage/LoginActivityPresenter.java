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

    /*
    * Check if basic params for login fields (email and password) meet basic criteria
    * If so then attempt to authenticate the user with firebase
    * otherwise set an error notice in the problem field
    *
    * The basic criteria are indicated as so:
    * - email isn't empty
    * - password isn't empty
    * - password is at least 6 characters long
    * */
    public void Login(String email, String password){
        if (email.isEmpty() && password.isEmpty()) {
            view.ShowPopupMessage("Email and Password are required.");
            return;
        }
        if (email.isEmpty()) {
            view.ShowPopupMessage("Email is required.");
            return;
        }
        if (password.isEmpty()) {
            view.ShowPopupMessage("Password is required.");
            return;
        }
        if (password.length() < 6) {
            view.ShowPopupMessage("Password must be at least 6 characters long.");
            return;
        }
        model.LoginUser(this, email, password);
    }

    /*
    Show a general error message as a Toast
     */
    public void ShowErrorPopup(String message) {
        view.ShowPopupMessage(message);
    }

    /*
        Given an Activity Class, TargetPage, redirects the current view to the target activity/page
     */
    public <T extends AppCompatActivity> Intent PageRedirect(Class<T> TargetPage) {
        Intent tmpIntent = new Intent(view, TargetPage);
        view.startActivity(tmpIntent);
        return tmpIntent;
    }
}
