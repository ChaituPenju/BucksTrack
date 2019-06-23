package com.chaitupenjudcoder;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.chaitupenjudcoder.buckstrack.R;
import com.chaitupenjudcoder.buckstrack.databinding.ActivitySignUpBinding;
import com.chaitupenjudcoder.datapojos.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding signupUtil;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference1, mReference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupUtil = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        //reference to users json
        mReference1 = mDatabase.getReference("users");
        //reference to data json
        mReference2 = mDatabase.getReference("data");
        signupUtil.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignup();
            }
        });
    }

    //initialize firebase with some basic data
    private void initFirebaseData(String userID) {
        String[] categoriesIncome = {"salary", "loan", "lottery"};
        String[] categoriesExpense = {"food", "travel", "shopping", "entertainment"};
        HashMap<String, String> categories = new HashMap<>();
        String key = "";
        for (String category: categoriesIncome) {
            key = mReference2.push().getKey();
            categories.put(key, category);
        }
        categories.clear();
        mReference2.child(userID).child("categories").child("income").setValue(categories);
        for (String category: categoriesExpense) {
            key = mReference2.push().getKey();
            categories.put(key, category);
        }
        mReference2.child(userID).child("categories").child("expense").setValue(categories);
    }
    //form validation function
    public void userSignup() {
        String email = signupUtil.etEmail.getText().toString();
        String password = signupUtil.etPassword.getText().toString();

        if (email.isEmpty()) {
            signupUtil.etEmail.setError("Email should not be empty");
            signupUtil.etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupUtil.etEmail.setError("Email is invalid");
            signupUtil.etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            signupUtil.etPassword.setError("Password should not be empty");
            signupUtil.etPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //get user display name
                    String displayName = signupUtil.etFullname.getText().toString();
                    //set user display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
                    //update user profile
                    user.updateProfile(profileUpdates);
                    Long tsLong = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    String ts = tsLong.toString();
                    User userObj = new User(signupUtil.etEmail.getText().toString(), signupUtil.etFullname.getText().toString(), ts, ts);
                    mReference1.child(userID).setValue(userObj);
                    initFirebaseData(userID);
                    Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUpActivity.this, "Email Id is Already Registered", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(SignUpActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
