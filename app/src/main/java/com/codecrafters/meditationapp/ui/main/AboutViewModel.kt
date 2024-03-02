package com.codecrafters.meditationapp.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AboutViewModel : ViewModel() {
    // Firebase database reference
    lateinit var database: DatabaseReference

    // User data fields
    var photoURL: String = ""
    var firstname: String = ""
    var lastname: String = ""
    var email: String = ""

    // LiveData objects to observe changes
    var liveFirstName = MutableLiveData<String>()
    var liveProfilePicture = MutableLiveData<String>()

    // Function to retrieve user's profile picture URL from Firebase
    fun getProfilePictureField() {
        val uid = FirebaseAuth.getInstance().uid
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Update live data with profile picture URL
                liveProfilePicture.value = snapshot.child("profileImageUrl").value.toString()
            } else {
                Log.d("AboutViewModel", "Failed to load Photo")
            }
        }.addOnFailureListener {
            Log.d("AboutViewModel", "Failed to load Photo")
        }
    }

    // Function to retrieve user's first name from Firebase
    fun getFirstName(): String {
        val uid = FirebaseAuth.getInstance().uid
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Update live data with first name
                firstname = snapshot.child("firstname").value.toString()
                liveFirstName.value = firstname
                Log.d("AboutViewModel", "First name: $firstname")
            } else {
                Log.d("AboutViewModel", "Failed to load First Name")
            }
        }.addOnFailureListener {
            Log.d("AboutViewModel", "Failed to load First Name")
        }
        return firstname
    }

    // Function to retrieve user's last name from Firebase
    fun getLastName() {
        val uid = FirebaseAuth.getInstance().uid
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                lastname = snapshot.child("lastname").value.toString()
            } else {
                Log.d("AboutViewModel", "Failed to load Last Name")
            }
        }.addOnFailureListener {
            Log.d("AboutViewModel", "Failed to load Last Name")
        }
    }

    // Function to retrieve user's email from Firebase
    fun getEmail() {
        val uid = FirebaseAuth.getInstance().uid
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uid!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                email = snapshot.child("username").value.toString()
            } else {
                Log.d("AboutViewModel", "Failed to load Email")
            }
        }.addOnFailureListener {
            Log.d("AboutViewModel", "Failed to load Email")
        }
    }
}
