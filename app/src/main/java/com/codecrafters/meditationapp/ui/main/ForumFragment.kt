package com.codecrafters.meditationapp.ui.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.codecrafters.meditationapp.*
import com.codecrafters.meditationapp.models.PostHistory
import kotlinx.android.synthetic.main.fragment_forum.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ForumFragment : Fragment() {
    // Adapter for the RecyclerView
    private var adapter: RecyclerAdapter? = null

    // List to hold post history items
    var items : MutableList<PostHistory> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)

        // Set up RecyclerView with LinearLayoutManager
        view.recyclerV.layoutManager = LinearLayoutManager(context)
        items = arrayListOf()

        // Reference to "posts" node in Firebase Database, ordered by posted date
        val ref = FirebaseDatabase.getInstance().getReference("posts").orderByChild("posteddate")
        ref.keepSynced(true)

        // ValueEventListener to retrieve data from Firebase Database
        val postListener : ValueEventListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (courtSnapshot in dataSnapshot.children) {
                    // Retrieve post data from dataSnapshot
                    val uid = courtSnapshot.child("uid").value as String?
                    val posteddate = courtSnapshot.child("posteddate").value as String?
                    val postbody = courtSnapshot.child("postbody").value as String?

                    // Create PostHistory object and add it to the list
                    val newPost = PostHistory(uid!!, posteddate!!, postbody!!)
                    items.add(newPost)
                }

                // Set up RecyclerView adapter and notify changes
                adapter = activity?.let { RecyclerAdapter(it.applicationContext, items) }
                adapter?.notifyDataSetChanged()
                view.recyclerV?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        }
        ref.addValueEventListener(postListener)

        // Button click listener to save post to Firebase Database
        view.send_button.setOnClickListener() {
            if (view.edittext_chat.text.isNotEmpty()) {
                savePostToFirebaseDatabase(view)
            }
        }
        return view
    }

    // Function to save post to Firebase Database
    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePostToFirebaseDatabase(view: View) {
        val postId = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/posts/$postId")
        val uid = FirebaseAuth.getInstance().uid
        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        // Create PostHistory object with user ID, current date, and post body
        val post = PostHistory(uid.toString(), currentDate, view.edittext_chat.text.toString())

        // Save post to Firebase Database
        ref.setValue(post)
            .addOnSuccessListener {
                Log.d(LoginAddUser.TAG, "Post saved to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(LoginAddUser.TAG, "Failed to save post: ${it.message}")
            }

        // Clear the text field after posting
        view.edittext_chat.text.clear()
    }
}
