package com.codecrafters.meditationapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecrafters.meditationapp.R
import kotlinx.android.synthetic.main.activity_videos_list.view.*

// MyAdapter class responsible for binding video data to views in RecyclerView
class MyAdapter (
    var videosList: ArrayList<Videos>, // List of video items
    val itemClickListener: ItemClickListener // Listener for item click events
): RecyclerView.Adapter<MyAdapter.ListViewHolder>(){

    // Interface for handling item click events
    interface ItemClickListener {
        fun onItemClick(position: Int) // Function to handle item click event
    }

    // ViewHolder class to hold references to views within each item
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            // Initialize OnClickListener for the item view
            itemView.setOnClickListener{
                // When item is clicked, call onItemClick function of ItemClickListener
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    // Called when RecyclerView needs a new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        // Inflate the layout for each item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_videos_list,parent,false)
        return ListViewHolder(view) // Return the newly created ViewHolder
    }

    // Called to bind data to the views within each ViewHolder
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // Set the image resource, title, and duration of the video item to the corresponding views
        holder.itemView.imageRecyclerView.setImageResource(videosList[position].image)
        holder.itemView.textRecyclerView1.text = "${videosList[position].title}"
        holder.itemView.textRecyclerView3.text = "Duration: ${videosList[position].duration}"
    }

    // Return the total number of items in the RecyclerView
    override fun getItemCount(): Int {
        return videosList.size
    }
}
