package com.example.authenticationguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = Firebase.auth
        val userIdTextView: TextView = findViewById(R.id.user_id_textView)
        val signOutButton: Button = findViewById(R.id.sign_out_button)
        val addPostImageView: ImageView = findViewById(R.id.addpost_imageView)
        val profileImageView: ImageView = findViewById(R.id.profile_imageView)
        val homeRecyclerView: RecyclerView = findViewById(R.id.home_recyclerView)
        val goToFollowsImage: ImageView = findViewById(R.id.go_to_follows_imageView)
        val searchButton: Button = findViewById(R.id.search_button)
        val searchEditText: TextView = findViewById(R.id.search_editText)

        val posts = mutableListOf<Post>()

        val adapter = PostAdapter(posts)
        homeRecyclerView.adapter = adapter
        homeRecyclerView.layoutManager = LinearLayoutManager(this)

        val db = Firebase.firestore
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Home", "${document.id} => ${document.data}")
                    posts.add(
                        Post(document.id, document.data["uid"].toString(), document.data["text"].toString())
                    )

                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("Home", "Error getting documents: ", exception)
            }
        searchButton.setOnClickListener {
            db.collection("posts")
                .whereGreaterThanOrEqualTo("text", searchEditText.text.toString())
                .get()
                .addOnSuccessListener { result ->
                    posts.clear()
                    for (document in result) {
                        Log.d("Home", "${document.id} => ${document.data}")
                        posts.add(
                            Post(document.id, document.data["uid"].toString(), document.data["text"].toString())
                        )

                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("Home", "Error getting documents: ", exception)
                }
        }
        goToFollowsImage.setOnClickListener {
            val intent = Intent(this, FollowsActivity::class.java)
            startActivity(intent)
        }

        addPostImageView.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }
        profileImageView.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val currentUser = auth.currentUser

        signOutButton.setOnClickListener {
            Firebase.auth.signOut()
            //val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
            finish()
        }

        if(currentUser != null)
        {
            userIdTextView.text = currentUser.email.toString()
        }
        else
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}