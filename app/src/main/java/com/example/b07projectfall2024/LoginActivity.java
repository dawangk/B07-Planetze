package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText EmailField, PasswordField;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        Button buttonLogin = findViewById(R.id.loginButton);
        Button registerRedirect = findViewById(R.id.Login_registerRedirect);
        Button ForgotPassword = findViewById(R.id.Login_forgotPasswordButton);

        EmailField = findViewById(R.id.loginEmailField);
        PasswordField = findViewById(R.id.loginPasswordField);

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterFragment.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordFragment.class));
            }
        });
    }

    private void loginUser() {
        String email = EmailField.getText().toString().trim();
        String password = PasswordField.getText().toString().trim();

        if (email.isEmpty()) {
            EmailField.setError("Email is required");
            EmailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            PasswordField.setError("Password is required");
            PasswordField.requestFocus();
            return;
        }else if (password.length()<6) {
            PasswordField.setError("Password must be at least 6 characters in length");
            PasswordField.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null && !user.isEmailVerified()){
                                mAuth.signOut();
                            }else if(user!=null){
                                db.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        UserInformation.setUserInfo(documentSnapshot);
                                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                    }
                                });
                            }
                        }
                    }
                });
    }

}
