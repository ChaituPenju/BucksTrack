package com.chaitupenjudcoder;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivityLoginBinding;
import com.chaitupenjudcoder.firebasehelpers.BucksInputValidationHelper;
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
        loginUtil.btnLogin.setOnClickListener(v -> {
            userLogin();
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
        BucksInputValidationHelper validationHelper = new BucksInputValidationHelper();

        if (!validationHelper.inputValidator(loginUtil.etEmailWrapper, 9999) | !validationHelper.inputValidator(loginUtil.etPasswordWrapper, 999)) {
            return;
        }

        String email = loginUtil.etEmail.getText().toString();
        String password = loginUtil.etPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        //get current user uid
                        String userID = user.getUid();
                        //get current time in seconds
                        Long tsLong = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                        String ts = tsLong.toString();
                        //update the last login id to latest value
                        FirebaseDatabase.getInstance().getReference("users/" + userID).child("last_login").setValue(ts);
                        finish();
                        Intent in = new Intent(LoginActivity.this, BucksActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        Toast.makeText(LoginActivity.this, "User Login Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goToSignup(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
