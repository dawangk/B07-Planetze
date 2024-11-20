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

    /*
     * Check if basic params for email field meet basic criteria
     * If so then attempt to send a password reset email to the user
     * otherwise set an error notice in the problem field
     *
     * The basic criteria are indicated as so:
     * - email isnt empty
     * */
    public void CheckSendEmail(String email){
        if (email.isEmpty()) {
            view.EmailFieldError("Email is required");
            return;
        }
        model.sendEmail(this, email);
    }

    /*
    Creates a Toast notification with the provided message Msg
     */
    public void MakeToast(String Msg){
        Toast.makeText(view, Msg, Toast.LENGTH_SHORT).show();
    }

    /*
        Given an Activity Class, TargetPage, redirects the current view to the target activity/page
     */
    public <T extends AppCompatActivity> void PageRedirect(Class<T> TargetPage) {
        view.startActivity(new Intent(view, TargetPage));
    }
}
