package com.codecrafters.meditationapp

// Importing necessary Android libraries
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.codecrafters.meditationapp.models.PostHistory
import com.squareup.picasso.Picasso

// Defining the RecyclerAdapter class
class RecyclerAdapter(var context: Context, items: List<PostHistory>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder?>() {

    // Initializing variables
    lateinit var items: List<PostHistory>
    private val mLayoutInflater: LayoutInflater
    private val count: Long = 1
    // Initializing database reference
    private lateinit var database: DatabaseReference

    // Initialization block
    init {
        this.items = items
        mLayoutInflater = LayoutInflater.from(context)
        FirebaseAuth.getInstance().uid // Ensuring Firebase authentication
    }

    // Inner ViewHolder class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var postbody: TextView
        var posteddate: TextView
        var image: ImageView

        init {
            // Binding views
            username = itemView.findViewById<View>(R.id.name) as TextView
            postbody = itemView.findViewById<View>(R.id.chatContent) as TextView
            posteddate = itemView.findViewById<View>(R.id.date) as TextView
            image = itemView.findViewById<View>(R.id.imageview_chat) as ImageView
        }
    }

    // onCreateViewHolder method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            mLayoutInflater.inflate(R.layout.post_history_items, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder method
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Setting data to views
        holder.username.setText(items[position].uid)
        holder.postbody.setText(items[position].postbody)
        holder.posteddate.setText(items[position].posteddate)
        val uid = FirebaseAuth.getInstance().uid

        // Getting user profile image from Firebase database
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(items[position].uid!!).get().addOnSuccessListener {
            if (it.exists()) {
                Picasso.get().load(it.child("profileImageUrl").value.toString()).into(holder.image)
            } else {
                Toast.makeText(context, "User doesn't exist.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Fail!!! User doesn't exist.", Toast.LENGTH_SHORT).show()
        }

        // Changing text color if the post is made by the current user
        if(FirebaseAuth.getInstance().uid == items[position].uid) {
            holder.postbody.setTextColor(R.color.white)
        }
    }

    // getItemCount method
    override fun getItemCount(): Int {
        return items.size
    }

    // getItemId method
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // getItemViewType method
    override fun getItemViewType(position: Int): Int {
        return position
    }

    // Companion object
    companion object {
        private const val TAG = "RecyclerAdapter"
    }
}
