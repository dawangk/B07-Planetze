package com.example.b07projectfall2024.ForgotPasswordPage;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.b07projectfall2024.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordModel {

    private FirebaseAuth mAuth;
    public ForgotPasswordModel(){
        mAuth = FirebaseAuth.getInstance();
    }
    public void sendEmail(ForgotPasswordPresenter presenter, String email){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    presenter.MakeToast("Sent Password Reset Email If Email Exists");
                }else{
                    presenter.MakeToast("Error: "+task.getException().getMessage());
                }
            }
        });
    }
}
