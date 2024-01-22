package com.example.authenticationguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val posts = mutableListOf<Post>()

        val profileRecyclerView: RecyclerView = findViewById(R.id.profile_recyclerView)
        val adapter = PostAdapter(posts)
        profileRecyclerView.adapter = adapter
        profileRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("posts")
            .whereEqualTo("uid", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Profile", "${document.id} => ${document.data}")
                    posts.add(
                        Post(document.id, document.data["uid"].toString(), document.data["text"].toString())
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Profile", "Error getting documents: ", exception)
            }
    }
}