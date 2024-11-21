package com.example.b07projectfall2024.LoginPage;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public LoginActivityModel(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void LoginUser(LoginActivityPresenter presenter, String email, String password) {
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
                                        presenter.PageRedirect(MainActivity.class);
                                    }
                                });
                            }
                        }

                        else {
                            presenter.onLoginFailed("Invalid email and/or password");
                        }
                    }
                });
    }


}
