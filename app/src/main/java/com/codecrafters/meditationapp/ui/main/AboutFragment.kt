package com.codecrafters.meditationapp.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.codecrafters.meditationapp.LoginActivity
import com.codecrafters.meditationapp.R
import kotlinx.android.synthetic.main.fragment_about.view.*
import java.net.URL
import java.util.*
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class AboutFragment : Fragment() {
    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)

        // Get and display user's first name
        viewModel.getFirstName()
        viewModel.liveFirstName.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            // Update the UI with the user's first name
            view.name.text = "Hi ${it.toString()}!"
        })

        // Get and display user's profile picture
        viewModel.getProfilePictureField()
        viewModel.liveProfilePicture.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            // Load user's profile picture using Picasso library
            Picasso.get().load(it.toString()).into(view.avatar)
        })

        // Set current date on the calendar view
        val calendar = Calendar.getInstance()
        view.cal.date = calendar.timeInMillis

        // Handle logout button click
        view.logout.setOnClickListener {
            // Sign out the current user
            FirebaseAuth.getInstance().signOut()

            // Navigate to the LoginActivity
            startActivity(Intent(context, LoginActivity::class.java))

            // Remove the completion flag for onboarding
            var preferences: SharedPreferences? = context?.getSharedPreferences("ONBOARD", Context.MODE_PRIVATE)
            preferences?.edit()?.remove("ISCOMPLETE")?.commit()

            // Finish the current activity
            activity?.finish()
        }

        return view
    }
}
