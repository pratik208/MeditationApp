package com.codecrafters.meditationapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.meditationapp.R

// Fragment to display a list of learning resources
class LearnFragment : Fragment(), MyAdapter.ItemClickListener {
    private lateinit var viewModel: LearnViewModel

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_learn, container, false)

        // Initialize the ViewModel associated with this fragment
        viewModel = ViewModelProvider(this).get(LearnViewModel::class.java)

        // Get a reference to the RecyclerView in the layout
        var rv = view.findViewById<RecyclerView>(R.id.recyclerView1)
        rv.layoutManager = LinearLayoutManager(context)

        // Create an instance of the adapter with the data from the ViewModel
        val adapter = MyAdapter(viewModel.getData(), this)

        // Set the adapter for the RecyclerView
        rv.adapter = adapter

        return view // Return the root view of the fragment layout
    }

    // Callback method invoked when an item in the RecyclerView is clicked
    override fun onItemClick(position: Int) {
        // Create an Intent to start the VideosView activity
        val intent = Intent(context, VideosView::class.java)

        // Pass data associated with the clicked item to the VideosView activity
        intent.putExtra("videos", viewModel.getData()[position])

        // Start the VideosView activity
        startActivity(intent)
    }
}
