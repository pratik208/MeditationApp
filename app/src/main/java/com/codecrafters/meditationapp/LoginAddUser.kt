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

    companion object {
        val TAG = "LoginAddUserActivity"
    }

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

        firstNameEdt = findViewById(R.id.ptxt_firstname)
        lastNameEdt = findViewById(R.id.ptxt_lastname)
        emailEdt = findViewById(R.id.ptxt_email)
        passwordEdt = findViewById(R.id.ptxtpassword)
        cpasswordEdt = findViewById(R.id.ptxt_cpassword)
        createuserBtn = findViewById(R.id.btn_createuser)
        loadingPB = findViewById(R.id.progressbar)
        loginTV = findViewById(R.id.idTVLogin)
        mAuth = FirebaseAuth.getInstance()

        loginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        createuserBtn.setOnClickListener {
            performRegister()
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

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

    private fun performRegister() {
        val fname: String = firstNameEdt.text.toString()
        val lname: String = lastNameEdt.text.toString()
        val email: String = emailEdt.text.toString()
        val pwd: String = passwordEdt.text.toString()
        val cpwd: String = cpasswordEdt.text.toString()

        if (!pwd.equals(cpwd)) {
            Toast.makeText(this, "Please check both passwords", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(cpwd)) {
            Toast.makeText(this, "Please add fully information..", Toast.LENGTH_SHORT).show()
        } else {
            loadingPB.visibility = View.VISIBLE
            mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User Created...", Toast.LENGTH_SHORT).show()
                        uploadImageToFirebaseStorage()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                    loadingPB.visibility = View.GONE
                }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = mAuth.uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, firstNameEdt.text.toString(), lastNameEdt.text.toString(), emailEdt.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }
}

data class User(val uid: String, val firstname: String, val lastname: String, val username: String, val profileImageUrl: String)
