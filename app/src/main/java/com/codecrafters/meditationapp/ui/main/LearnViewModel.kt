package com.codecrafters.meditationapp.ui.main

import androidx.lifecycle.ViewModel
import com.codecrafters.meditationapp.R

// ViewModel class responsible for managing data related to learning resources
class LearnViewModel : ViewModel() {
    // ArrayList to hold video data
    var videos = ArrayList<Videos>()

    // Method to populate the ArrayList with sample video data
    fun fillData() {
        // Create instances of Videos and add them to the ArrayList
        val video1: Videos = Videos("https://stoptrafficking.mn/wp-content/videos/tm01.mp4","How To Meditate For Beginners (Animated)","How To Meditate For Beginners! In this video, I'm going to tell you, where to meditate, how to meditate, how to stop thinking, how long to meditate for, even how long before you start seeing the benefits. ", "05:36", R.drawable.meditation3)
        val video2: Videos = Videos("https://stoptrafficking.mn/wp-content/videos/tm02.mp4","Alan Watts - Guided Meditation (Awakening The Mind)","This is a 14-minute Alan Watts guided meditation video discussing his method of establishing a meditative state and reaching self-awareness.\n", "14.45", R.drawable.meditation2)
        // Add more video instances as needed

        // Add the videos to the ArrayList
        videos.add(video1)
        videos.add(video2)
        // Add more videos as needed
    }

    // Method to get the list of videos
    fun getData(): ArrayList<Videos> {
        // Call fillData() to populate the ArrayList if not already populated
        fillData()
        // Return the ArrayList of videos
        return videos
    }
}
