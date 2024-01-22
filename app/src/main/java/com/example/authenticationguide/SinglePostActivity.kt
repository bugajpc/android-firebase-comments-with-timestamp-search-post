package com.example.authenticationguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SinglePostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)

        auth = Firebase.auth
        val postId = intent.getStringExtra("postId")

        val postText: TextView = findViewById(R.id.singlePost_text)
        val singlePostRecyclerView: RecyclerView = findViewById(R.id.singlePost_recyclerView)
        val editComment: EditText = findViewById(R.id.singlePost_editText)
        val addCommentButton: TextView = findViewById(R.id.singlePost_addComment_button)

        val comments = mutableListOf<Comment>()

        val db = Firebase.firestore
        db.collection("posts").document(postId!!)
        .get()
        .addOnSuccessListener { document ->
            if (document != null) {
                postText.text = document.data!!["text"].toString()
            }
        }
        .addOnFailureListener { exception ->
            postText.text = "Error"
        }

        val adapter = CommentAdapter(comments)
        singlePostRecyclerView.adapter = adapter
        singlePostRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("comments").whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("SinglePost", "${document.id} => ${document.data}")
                    comments.add(Comment(document.id,
                        document.data["uid"].toString(),
                        document.data["postId"].toString(),
                        document.data["text"].toString(),
                        document.data["timestamp"] as com.google.firebase.Timestamp))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("SinglePost", "Error getting documents: ", exception)
            }

        addCommentButton.setOnClickListener {
            val comment = hashMapOf(
                "text" to editComment.text.toString(),
                "uid" to auth.currentUser!!.uid,
                "postId" to postId,
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            db.collection("comments")
                .add(comment)
                .addOnSuccessListener { documentReference ->
                    Log.d("Comment", "DocumentSnapshot written with ID: ${documentReference.id}")
                    editComment.text.clear()
                    editComment.onEditorAction(0)
                    db.collection("comments").whereEqualTo("postId", postId)
                        .get()
                        .addOnSuccessListener { result ->
                            comments.clear()
                            for (document in result) {
                                Log.d("SinglePost", "${document.id} => ${document.data}")
                                comments.add(Comment(document.id,
                                    document.data["uid"].toString(),
                                    document.data["postId"].toString(),
                                    document.data["text"].toString(),
                                    document.data["timestamp"] as com.google.firebase.Timestamp))
                            }
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            Log.d("SinglePost", "Error getting documents: ", exception)
                        }
                }
                .addOnFailureListener { e ->
                    Log.w("Comment", "Error adding document", e)
                }
        }

    }
}