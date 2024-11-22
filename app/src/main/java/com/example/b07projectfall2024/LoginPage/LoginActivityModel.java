package com.example.b07projectfall2024.LoginPage;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.b07projectfall2024.MainActivity;
import com.example.b07projectfall2024.Questionnaire.QuestionnaireActivity;
import com.example.b07projectfall2024.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.b07projectfall2024.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityModel {
    private FirebaseAuth mAuth;
    private DatabaseReference db;

    public LoginActivityModel(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    /*
        Logs in the user by authenticating user information with firebase
        If authentication succeeds check if user email is verified
        if not sign user out immediately
        otherwise, retrieve and store user information from firestore db
     */
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
                                db.child("users").child(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        UserInformation.setUserInfo(documentSnapshot);

                                        DatabaseReference ref = db.child("users").child(user.getUid()).child("new_user");

                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {

                                                    if (snapshot.getValue(String.class).equals("yes")) {

                                                        ref.setValue("no");
                                                        presenter.PageRedirect(QuestionnaireActivity.class);

                                                    } else {
                                                        presenter.PageRedirect(MainActivity.class);
                                                    }
                                                }
                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });
    }


}
