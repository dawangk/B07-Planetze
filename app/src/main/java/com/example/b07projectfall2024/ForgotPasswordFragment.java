package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends AppCompatActivity {
    private EditText EmailField;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        EmailField = findViewById(R.id.ForgotPassword_ResetEmailField);

        Button ReturnToLogin = findViewById(R.id.ForgotPassword_BackLoginButton);
        Button SubmitEmail = findViewById(R.id.ForgotPassword_ResetEmailButton);

        ReturnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordFragment.this, LoginActivity.class));
            }
        });

        SubmitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail(){
        String email = EmailField.getText().toString().trim();

        if (email.isEmpty()) {
            EmailField.setError("Email is required");
            EmailField.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordFragment.this, "Sent Password Reset Email", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ForgotPasswordFragment.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
