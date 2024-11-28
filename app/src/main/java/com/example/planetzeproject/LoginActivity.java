package com.example.planetzeproject;

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

public class LoginActivity extends AppCompatActivity {

    private LoginPresenter presenter;
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin, back;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, textView2;

    // Checks if a user is already logged in on start if so takes user to homepage
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), EcoTrackerActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /*public void login() {
        System.out.println("WORKS!");
        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, EcoTrackerActivity.class);
        startActivity(intent);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        presenter = new LoginPresenter(new LoginModel(), this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);
        textView2 = findViewById(R.id.resetPass);
        back = findViewById(R.id.btnBack);


        // Takes you the sign up
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Takes you to password reset
        textView2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Login stuff
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email,password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());


                if (presenter.emailEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (presenter.passwordEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                presenter.signIn(email, password, getApplicationContext());


                /*if (presenter.signIn(email, password) == 1) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EcoTrackerActivity.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }
}