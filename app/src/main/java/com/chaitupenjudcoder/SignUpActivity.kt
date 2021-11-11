package com.chaitupenjudcoder

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivitySignUpBinding
import com.chaitupenjudcoder.datapojos.User
import com.chaitupenjudcoder.firebasehelpers.BucksInputValidationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    lateinit var signupUtil: ActivitySignUpBinding

    private val mAuth get() = FirebaseAuth.getInstance()
    private val mDatabase get() = FirebaseDatabase.getInstance()

    private var mReference1: DatabaseReference? = null
    private var mReference2: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupUtil = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        //reference to users json
        mReference1 = mDatabase.getReference("users")
        //reference to data json
        mReference2 = mDatabase.getReference("data")
        signupUtil.btnSignup.setOnClickListener { userSignup() }
    }

    //initialize firebase with some basic data
    private fun initFirebaseData(userID: String) {
        val categoriesIncome = arrayOf("salary", "loan", "lottery")
        val categoriesExpense = arrayOf("food", "travel", "shopping", "entertainment")
        val categories = HashMap<String?, String>()
        var key: String? = ""
        for (category in categoriesIncome) {
            key = mReference2!!.push().key
            categories[key] = category
        }
        mReference2!!.child(userID).child("categories").child("income").setValue(categories)
        categories.clear()
        for (category in categoriesExpense) {
            key = mReference2!!.push().key
            categories[key] = category
        }
        mReference2!!.child(userID).child("categories").child("expense").setValue(categories)
    }

    //form validation function
    private fun userSignup() {
        val validationHelper = BucksInputValidationHelper()
        if (!validationHelper.inputValidator(
                signupUtil.etFullnameWrapper,
                signupUtil.etFullnameWrapper.counterMaxLength
            ) or !validationHelper.inputValidator(
                signupUtil.etEmailWrapper, signupUtil.etEmailWrapper.counterMaxLength
            ) or !validationHelper.inputValidator(
                signupUtil.etPasswordWrapper, signupUtil.etPasswordWrapper.counterMaxLength
            ) or !validationHelper.inputValidator(
                signupUtil.etPasswordConfirmWrapper, signupUtil.etPasswordConfirmWrapper.counterMaxLength
            )
        ) {
            return
        }
        if (!validationHelper.passwordValidator(signupUtil.etPasswordWrapper, signupUtil.etPasswordConfirmWrapper)) {
            return
        }

        val email = signupUtil.etEmail.text.toString()
        val password = signupUtil.etPassword.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signupUtil.etEmail.error = "Email is invalid"
            signupUtil.etEmail.requestFocus()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val userID = user!!.uid
                    //get user display name
                    val displayName = signupUtil.etFullname.text.toString()
                    //set login time milliseconds
                    val tsLong = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
                    val ts = tsLong.toString()
                    val userObj = User(
                        signupUtil.etEmail.text.toString(),
                        signupUtil.etFullname.text.toString(),
                        ts,
                        ts
                    )
                    mReference1!!.child(userID).setValue(userObj)
                    initFirebaseData(userID)
                    //set user display name
                    val profileUpdates =
                        UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
                    //update user profile
                    user.updateProfile(profileUpdates).addOnCompleteListener {
                        Toast.makeText(this@SignUpActivity, "SignUp Successful", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Email Id is Already Registered",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@addOnCompleteListener
                    }
                    Toast.makeText(
                        this@SignUpActivity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}