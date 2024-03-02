package com.codecrafters.meditationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginForgotPassword : AppCompatActivity() {

    // Companion object holding a constant TAG for logging
    companion object {
        val TAG = "ForgotActivity"
    }

    // Views and FirebaseAuth instance
    lateinit var forgotbutton: Button
    lateinit var forgot_txt: TextView
    lateinit var loginTV: TextView
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_forgot_password)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Find views by their IDs
        forgot_txt = findViewById(R.id.forgottxt_email)
        forgotbutton = findViewById(R.id.btn_forgotPassword)

        // OnClickListener for the "Forgot Password" button
        forgotbutton.setOnClickListener() {
            // Get email from the text view
            val email: String = forgot_txt.text.toString().trim()
            // Print email (optional)
            print(email)

            // Send password reset email using FirebaseAuth
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Show success message
                        Toast.makeText(this, "Email sent successfully", Toast.LENGTH_LONG).show()
                        // Finish the activity
                        finish()
                    } else {
                        // Show error message if sending email fails
                        Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }

        // OnClickListener for the "Login" text view
        loginTV = findViewById(R.id.idTVLogin)
        loginTV.setOnClickListener() {
            // Start LoginActivity when "Login" text view is clicked
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }
    }
}
