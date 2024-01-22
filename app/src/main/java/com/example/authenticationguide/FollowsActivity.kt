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

class FollowsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follows)

        val followsRecyclerView: RecyclerView = findViewById(R.id.follows_recyclerView)
        auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val follows = mutableListOf<String>()
        val posts = mutableListOf<Post>()

        val adapter = PostAdapter(posts)
        followsRecyclerView.adapter = adapter
        followsRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("follows")
            .whereEqualTo("uid", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    follows.add(document.data["postId"].toString())
                }
                for(postId in follows) {

                }
                //go through each post and add to posts

                for(follow in follows) {
                    val docRef = db.collection("posts").document(follow)
                    docRef.get()
                        .addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                posts.add(
                                    Post(doc.id, doc.data!!["uid"].toString(), doc.data!!["text"].toString())
                                )
                                adapter.notifyDataSetChanged()
                                Log.d("TAG", "DocumentSnapshot data: ${doc.data}")
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Profile", "Error getting documents: ", exception)
            }
    }
}