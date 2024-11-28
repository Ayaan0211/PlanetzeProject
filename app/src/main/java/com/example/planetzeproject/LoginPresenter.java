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

import androidx.appcompat.app.AppCompatActivity;


public class LoginPresenter extends AppCompatActivity {


    private LoginModel model;
    private LoginActivity view;


    public LoginPresenter(LoginModel model, LoginActivity view) {
        this.model = model;
        this.view = view;
    }


    public boolean emailEmpty(String email) {
        return (TextUtils.isEmpty(email));
    }


    public boolean passwordEmpty(String password) {
        return (TextUtils.isEmpty(password));
    }


    public void signIn(String email, String password, Context context) {
        /*int foo = model.signInEmailPassword(email, password);
        System.out.println(foo);
*/
        //return foo;
        model.signInEmailPassword(email, password, context);
    }

    public void validSignIn() {
        /*Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), EcoTrackerActivity.class);
        startActivity(intent);
        finish();*/
        System.out.println("WORKS VALID!");
        //view.login();
    }

    public void invalidSignIn() {
        //Toast.makeText(view, "Authentication failed.", Toast.LENGTH_SHORT).show();
        //finish();
        System.out.println("WORKS INVALID!");
    }
}
