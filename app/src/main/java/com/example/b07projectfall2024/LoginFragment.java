package com.example.b07projectfall2024;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends FragmentTemplate {

    private EditText EmailField, PasswordField;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        Button buttonLogin = view.findViewById(R.id.loginButton);
        Button registerRedirect = view.findViewById(R.id.Login_registerRedirect);

        EmailField = view.findViewById(R.id.loginEmailField);
        PasswordField = view.findViewById(R.id.loginPasswordField);

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new RegisterFragment(), false);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(view);
            }
        });

        return view;
    }

    private void loginUser(View v) {
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
                                        loadFragment(new HomeFragment(), false);
                                    }
                                });
                            }
                        }
                    }
                });
    }

}
