package com.codecrafters.meditationapp.ui.main

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel class to manage data related to meditation and breathing exercises
class HomeViewModel: ViewModel() {
    var times: Int = 0; // Unused variable, can be removed
    lateinit var sharedPreferences: SharedPreferences // SharedPreferences to store data
    val MED_COUNT = "meditation_count" // Key for storing meditation count
    val MED_MINUTES = "meditation_minutes" // Key for storing total meditation minutes
    val BREATHE_COUNT = "breathe_count" // Key for storing breathing count
    val BREATHE_MINUTES = "breathe_minutes" // Key for storing total breathing minutes
    var test = MutableLiveData<String>() // Unused LiveData variable

    // Method to initialize the view model with a shared preferences file
    fun init(context: Context, prefName: String) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    // Method to get the count of completed meditation sessions from SharedPreferences
    fun getMeditationCount(): Int {
        return sharedPreferences.getInt(MED_COUNT, 0) // Returns the stored count, defaulting to 0 if not found
    }

    // Method to update the count of completed meditation sessions in SharedPreferences
    fun updateMeditationCount() {
        sharedPreferences.edit().putInt(MED_COUNT, getMeditationCount() + 1).apply() // Increments the count by 1 and saves it
    }

    // Method to get the total minutes of completed meditation sessions from SharedPreferences
    fun getMeditationMin(): Int {
        return sharedPreferences.getInt(MED_MINUTES, 0) // Returns the stored total minutes, defaulting to 0 if not found
    }

    // Method to update the total minutes of completed meditation sessions in SharedPreferences
    fun updateMeditationMinutes(minutes: Int) {
        sharedPreferences.edit().putInt(MED_MINUTES, getMeditationMin() + minutes).apply() // Adds the provided minutes to the total and saves it
    }

    // Method to get the count of completed breathing sessions from SharedPreferences
    fun getBreatheCount(): Int {
        return sharedPreferences.getInt(BREATHE_COUNT, 0) // Returns the stored count, defaulting to 0 if not found
    }

    // Method to update the count of completed breathing sessions in SharedPreferences
    fun updateBreatheCount() {
        sharedPreferences.edit().putInt(BREATHE_COUNT, getBreatheCount() + 1).apply() // Increments the count by 1 and saves it
    }

    // Method to get the total minutes of completed breathing sessions from SharedPreferences
    fun getBreatheMin(): Int {
        return sharedPreferences.getInt(BREATHE_MINUTES, 0) // Returns the stored total minutes, defaulting to 0 if not found
    }

    // Method to update the total minutes of completed breathing sessions in SharedPreferences
    fun updateBreatheMin(minutes: Int) {
        sharedPreferences.edit().putInt(BREATHE_MINUTES, getBreatheMin() + minutes).apply() // Adds the provided minutes to the total and saves it
    }
}
