package com.example.b07projectfall2024.RegisterPage;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.b07projectfall2024.LoginPage.LoginActivityView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivityModel {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    public RegisterActivityModel(){
        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    /*
    Attempt to create a new user with email, password and name.
    If task is successful and a new user is successfully created, send an email verification email
    as well as add the name of the user to the database with field name filled.

    As the firebase library automatically logs the device in after registration logout immediately after
    a successful registration and redirect to the login page.
     */
    public void RegisterUser(RegisterActivityPresenter presenter, String email, String password, String name){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!=null) {
                        user.sendEmailVerification();//send email

                        //Store user's name into the db
                        Map<String, String> m = new HashMap<String, String>();
                        m.put("name", name);
                        m.put("new_user", "yes");
                        m.put("Habits", "none");
                        m.put("AntiHabits", "none");
                        db.child("users").child(user.getUid()).setValue(m).addOnSuccessListener(
                                documentReference -> {
                                    presenter.PageRedirect(LoginActivityView.class);
                                    mAuth.signOut();//log user out
                                }
                        );
                    }

                }
            }
        });
    }

}
