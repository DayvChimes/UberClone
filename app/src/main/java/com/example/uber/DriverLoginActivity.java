package com.example.uber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration;
    private ProgressBar mprogressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth = FirebaseAuth.getInstance();

        mLogin = findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        mRegistration = findViewById(R.id.registration);
        mRegistration.setOnClickListener(this);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        mprogressBar = findViewById(R.id.progressBar);
        mprogressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(DriverLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.registration:
                startActivity(new Intent(this,  RegisterDriver.class));
                finish();
                break;

            case R.id.login:
                userLogin();
        }
    }

    private void userLogin() {
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();


        if(email.isEmpty()){
            mEmail.setError("Email is required!");
            mEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("Email should be legitimate");
            mEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            mPassword.setError("Min password Length is 6 characters");
            mPassword.requestFocus();
            return;
        }

        mprogressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(mAuth.getCurrentUser().getUid());
                    driverRef.setValue(mAuth.getCurrentUser().getEmail());
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }else{
                    Toast.makeText(DriverLoginActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                    mprogressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}