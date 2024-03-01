package com.codecrafters.meditationapp

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    // Tag for logging
    companion object {
        val TAG = "login"
    }

    // Declaring UI elements
    lateinit var inputEmail: TextInputEditText
    lateinit var inputPassword: TextInputEditText
    lateinit var loginbutton: Button
    lateinit var forgotbutton: TextView
    lateinit var registertv: TextView
    lateinit var loadingPB: ProgressBar
    lateinit var googleLoginBtn: SignInButton

    // Firebase Authentication instance
    lateinit var mAuth: FirebaseAuth

    // Google Sign-In variables
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var preferences: SharedPreferences
    private val REQ_ONE_TAP = 2  // Request code for one-tap sign-in

    // Flag to determine whether to show one-tap UI or not
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initializing UI elements
        inputEmail = input_email
        inputPassword = input_password
        loginbutton = findViewById(R.id.btn_login)
        registertv = findViewById(R.id.registerTV)
        forgotbutton = findViewById(R.id.forgotPassword)
        mAuth = FirebaseAuth.getInstance()
        loadingPB = findViewById(R.id.progressbar)
        googleLoginBtn = findViewById(R.id.sign_in_button)

        // Initialize Google SignInClient and setup sign-in request
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.firebaseid)) // Your server's client ID
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .setAutoSelectEnabled(true) // Automatically select credentials if only one available
            .build()

        // Set click listener for Google Sign-In button
        googleLoginBtn.setOnClickListener() {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    Log.d(TAG, e.localizedMessage)
                }
        }

        // Retrieve shared preferences for onboarding check
        preferences = getSharedPreferences("ONBOARD", Context.MODE_PRIVATE)

        // Set click listeners for Register and Forgot Password
        registertv.setOnClickListener() {
            startActivity(Intent(this, LoginAddUser::class.java))
        }
        forgotbutton.setOnClickListener() {
            startActivity(Intent(this, LoginForgotPassword::class.java))
        }

        // Set click listener for login button
        loginbutton.setOnClickListener() {
            val email: String = inputEmail.text.toString()
            val pwd: String = inputPassword.text.toString()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show()
            } else {
                // Authenticate user with Firebase
                mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        loadingPB.visibility = View.GONE
                        Toast.makeText(this, "Login Successful..", Toast.LENGTH_SHORT).show()
                        startMainActivity()
                    } else {
                        loadingPB.visibility = View.GONE
                        Toast.makeText(this, "Email or Password is wrong. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Handle the result of one-tap sign-in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Handle ID token, possibly authenticate with Firebase
                            Log.d(TAG, "Got ID token.")
                        }
                        else -> {
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    // Handle API exception
                }
            }
        }
    }

    // Redirect to MainActivity if user is already signed in
    override fun onStart() {
        super.onStart()
        var user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            startMainActivity()
        }
    }

    // Check if onboarding is completed
    fun isSeenOnboard(): Boolean {
        return preferences.getBoolean("ISCOMPLETE", false)
    }

    // Start MainActivity or OnboardingActivity based on onboarding completion
    private fun startMainActivity() {
        if (isSeenOnboard()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
        finish()
    }
}
