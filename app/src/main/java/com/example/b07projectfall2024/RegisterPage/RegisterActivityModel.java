package com.example.b07projectfall2024.RegisterPage;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivityModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public RegisterActivityModel(){
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void RegisterUser(RegisterActivityPresenter presenter, String email, String password, String name){
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
                                    presenter.PageRedirect(LoginActivityView.class);
                                    mAuth.signOut();
                                }
                        );
                    }

                }
            }
        });
    }

}
