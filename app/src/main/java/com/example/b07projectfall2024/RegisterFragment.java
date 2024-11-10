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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends AppCompatActivity {


    private EditText EmailField, PasswordField, NameField, VerifyPasswordField;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.Register_registerButton);
        Button loginRedirect = findViewById(R.id.Register_redirectLoginButton);


        EmailField = findViewById(R.id.Register_emailField);
        NameField = findViewById(R.id.Register_nameField);
        PasswordField = findViewById(R.id.Register_passwordField);
        VerifyPasswordField = findViewById(R.id.Register_verifyPasswordField);

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterFragment.this, LoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = NameField.getText().toString().trim();
        String email = EmailField.getText().toString().trim();
        String password = PasswordField.getText().toString().trim();
        String verifypassword = VerifyPasswordField.getText().toString().trim();


        if (email.isEmpty()) {
            EmailField.setError("Email is required");
            EmailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            PasswordField.setError("Password is required");
            PasswordField.requestFocus();
            return;
        }else if(password.length()<6) {
            PasswordField.setError("Password must be at least 6 characters in length");
            PasswordField.requestFocus();
            return;
        }

        if(name.isEmpty()){
            NameField.setError("Name is required");
            NameField.requestFocus();
            return;
        }

        if(verifypassword.isEmpty()){
            VerifyPasswordField.setError("Please verify your password");
            VerifyPasswordField.requestFocus();
            return;
        } else if(!verifypassword.equals(password)){
            VerifyPasswordField.setError("Passwords do not match");
            VerifyPasswordField.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        user.sendEmailVerification();
                        Map<String, String> m = new HashMap<String, String>();
                        m.put("name", name);
                        db.collection("users").document(user.getUid()).set(m).addOnSuccessListener(
                                documentReference -> {
                                    startActivity(new Intent(RegisterFragment.this, LoginActivity.class));
                                    mAuth.signOut();
                                }
                        );
                    }

                }
            }
        });
    }
}
