package com.chaitupenjudcoder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chaitupenjudcoder.buckstrack.R
import com.chaitupenjudcoder.buckstrack.databinding.ActivityLoginBinding
import com.chaitupenjudcoder.firebasehelpers.BucksInputValidationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var loginUtil: ActivityLoginBinding
    private val mAuth get() = FirebaseAuth.getInstance()
    private val mDb get() = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginUtil = DataBindingUtil.setContentView(this, R.layout.activity_login)


        loginUtil.btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userLogin()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null) {
            finish()
            Intent(this@LoginActivity, BucksActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    //login
    private suspend fun userLogin() {
        val validationHelper = BucksInputValidationHelper()

        if (!validationHelper.inputValidator(loginUtil.etEmailWrapper, 9999)
            or !validationHelper.inputValidator(loginUtil.etPasswordWrapper, 999)) {
            return
        }

        val email = loginUtil.etEmail.text.toString()
        val password = loginUtil.etPassword.text.toString()

        kotlin.runCatching {
            mAuth.signInWithEmailAndPassword(email, password).await()
        }.onSuccess {
            setLastLoginValueAndIntent()
        }.onFailure { exception ->
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goToSignup() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private suspend fun setLastLoginValueAndIntent() {
        withContext(Dispatchers.Main) {
            //get current user uid
            val userID = mAuth.currentUser?.uid
            //get current time in seconds
            val ts = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
            //update the last login id to latest value
            mDb.getReference("users/$userID").child("last_login").setValue(ts)
            finish()

            Intent(this@LoginActivity, BucksActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            Toast.makeText(this@LoginActivity, "User Login Success", Toast.LENGTH_SHORT).show()
        }
    }
}