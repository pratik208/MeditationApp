package com.codecrafters.meditationapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login_add_user.*
import java.util.*

class LoginAddUser : AppCompatActivity() {

    // Tag for logging
    companion object {
        val TAG = "LoginAddUserActivity"
    }

    // Declare UI elements
    lateinit var firstNameEdt: TextInputEditText
    lateinit var lastNameEdt: TextInputEditText
    lateinit var emailEdt: TextInputEditText
    lateinit var passwordEdt: TextInputEditText
    lateinit var cpasswordEdt: TextInputEditText
    lateinit var loginTV: TextView
    lateinit var createuserBtn: Button
    lateinit var loadingPB: ProgressBar
    lateinit var mAuth: FirebaseAuth
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_add_user)

        // Initialize UI elements
        firstNameEdt = findViewById(R.id.ptxt_firstname)
        lastNameEdt = findViewById(R.id.ptxt_lastname)
        emailEdt = findViewById(R.id.ptxt_email)
        passwordEdt = findViewById(R.id.ptxtpassword)
        cpasswordEdt = findViewById(R.id.ptxt_cpassword)
        createuserBtn = findViewById(R.id.btn_createuser)
        loadingPB = findViewById(R.id.progressbar)
        loginTV = findViewById(R.id.idTVLogin)
        mAuth = FirebaseAuth.getInstance()

        // Set click listener for switching to login activity
        loginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for create user button
        createuserBtn.setOnClickListener {
            performRegister()
        }

        // Set click listener for selecting profile picture
        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    // Handle result of selecting profile picture
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
        }
    }

    // Perform user registration
    private fun performRegister() {
        val fname: String = firstNameEdt.text.toString()
        val lname: String = lastNameEdt.text.toString()
        val email: String = emailEdt.text.toString()
        val pwd: String = passwordEdt.text.toString()
        val cpwd: String = cpasswordEdt.text.toString()

        // Validate user input
        if (!pwd.equals(cpwd)) {
            Toast.makeText(this, "Please check both passwords", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cpwd)) {
            Toast.makeText(this, "Please add fully information..", Toast.LENGTH_SHORT).show()
        } else {
            // Show progress bar and create user with Firebase Authentication
            loadingPB.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // User created successfully, upload profile picture (if selected) and navigate to login activity
                        Toast.makeText(this, "User Created...", Toast.LENGTH_SHORT).show()
                        uploadImageToFirebaseStorage()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        // Failed to create user, show error message
                        Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                    loadingPB.visibility = View.GONE
                }
        }
    }
    // Function to upload the selected image to Firebase Storage
    private fun uploadImageToFirebaseStorage() {
        // Check if a photo is selected
        if (selectedPhotoUri == null) return

        // Generate a unique filename for the image
        val filename = UUID.randomUUID().toString()

        // Get a reference to the Firebase Storage location where the image will be stored
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        // Upload the selected image to Firebase Storage
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // If the upload is successful, log the metadata path
                Log.d(TAG, "Successfully uploaded image: ${taskSnapshot.metadata?.path}")

                // Retrieve the download URL of the uploaded image
                ref.downloadUrl.addOnSuccessListener { uri ->
                    // Once the download URL is obtained, save the user details along with the image URL to Firebase Database
                    saveUserToFirebaseDatabase(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                // If the upload fails, log the error message
                Log.d(TAG, "Failed to upload image to storage: ${e.message}")
            }
    }

    // Function to save the user details to Firebase Database
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        // Get the current user's ID
        val uid = mAuth.uid ?: ""

        // Get a reference to the Firebase Database location where the user details will be stored
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // Create a User object with the user's details and profile image URL
        val user = User(uid, firstNameEdt.text.toString(), lastNameEdt.text.toString(), emailEdt.text.toString(), profileImageUrl)

        // Save the user object to Firebase Database
        ref.setValue(user)
            .addOnSuccessListener {
                // If the user data is successfully saved, log a success message
                Log.d(TAG, "Successfully saved user to Firebase Database")
            }
            .addOnFailureListener { e ->
                // If the user data fails to save, log the error message
                Log.d(TAG, "Failed to set value to database: ${e.message}")
            }
    }

    // Data class representing a user
    data class User(val uid: String, val firstname: String, val lastname: String, val username: String, val profileImageUrl: String)}
