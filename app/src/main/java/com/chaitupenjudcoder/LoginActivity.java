package com.chaitupenjudcoder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding loginUtil;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginUtil = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginUtil.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                userLogin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            finish();
            Intent in = new Intent(LoginActivity.this, BucksActivity.class);
            startActivity(in);
        }
    }
    //login
    private void userLogin() {

        String email = loginUtil.etEmail.getText().toString();
        String password = loginUtil.etPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            //get current user uid
                            String userID = user.getUid();
                            //get current time in seconds
                            Long tsLong = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                            String ts = tsLong.toString();
                            //update the last login id to latest value
                            FirebaseDatabase.getInstance().getReference("users/"+userID).child("last_login").setValue(ts);
                            finish();
                            Intent in = new Intent(LoginActivity.this, BucksActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(in);
                            Toast.makeText(LoginActivity.this, "User Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToSignup(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
