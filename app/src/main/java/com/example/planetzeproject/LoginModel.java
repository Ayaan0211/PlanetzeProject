package com.example.planetzeproject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginModel extends AppCompatActivity {


    private LoginPresenter presenter = new LoginPresenter(this, new LoginActivity());
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int flag = 0;


    public LoginModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser checkUserStatus() {
        return mAuth.getCurrentUser();
    }

    public void signInEmailPassword(String email, String password, Context context) {
        //mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            System.out.println("true");
                            //flag = 1;
                            //Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            /*Intent intent = new Intent(context, EcoTrackerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);*/
                            /*startActivity(intent);
                            finish();*/
                            presenter.validSignIn(context);
                        } else {
                            // If sign in fails, display a message to the user.
                            /*Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                            presenter.invalidSignIn(context);
                            //presenter.invalidSignIn();
                        }
                    }
                });
        //System.out.println(flag);
        //return flag;
    }


}
