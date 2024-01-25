package com.example.authenticationguide

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentAdapter(val comments: MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth
    val user = auth.currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val dateTextView: TextView = holder.itemView.findViewById(R.id.date_textView)
        val commentTextView: TextView = holder.itemView.findViewById(R.id.comment_text_textView)
        val userTextView: TextView = holder.itemView.findViewById(R.id.comment_user_textView)
        val likeImage: ImageView = holder.itemView.findViewById(R.id.comment_like_image)
        val dislikeImage: ImageView = holder.itemView.findViewById(R.id.comment_dislike_image)
        val likesCounter: TextView = holder.itemView.findViewById(R.id.likes_count_textView)


        dateTextView.text = comments[position].timestamp.toDate().toString()
        commentTextView.text = comments[position].text
        //userTextView.text = comments[position].uid

        db.collection("likes")
            .document(user!!.uid + ":" + comments[position].commentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if(document.data!!["like"].toString().toInt() == 1) {
                        likeImage.setImageResource(R.drawable.baseline_arrow_drop_up_blue_24)
                    } else {
                        dislikeImage.setImageResource(R.drawable.baseline_arrow_drop_down_red_24)
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        db.collection("likes")
            .whereEqualTo("commentId", comments[position].commentId)
            .get()
            .addOnSuccessListener { documents ->
                var likes = 0
                for (document in documents) {
                    likes += document.data["like"].toString().toInt()
                }
                likesCounter.text = likes.toString()
            }

        likeImage.setOnClickListener {
            val newLike = hashMapOf(
                "uid" to user!!.uid,
                "commentId" to comments[position].commentId,
                "like" to 1
            )
            db.collection("likes").document(user!!.uid + ":" + comments[position].commentId)
                .set(newLike)
                .addOnSuccessListener {
                    likeImage.setImageResource(R.drawable.baseline_arrow_drop_up_blue_24)
                    dislikeImage.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                    db.collection("likes")
                        .whereEqualTo("commentId", comments[position].commentId)
                        .get()
                        .addOnSuccessListener { documents ->
                            var likes = 0
                            for (document in documents) {
                                likes += document.data["like"].toString().toInt()
                            }
                            likesCounter.text = likes.toString()
                        }
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error writing document")
                }
        }
        dislikeImage.setOnClickListener {
            val newLike = hashMapOf(
                "uid" to user!!.uid,
                "commentId" to comments[position].commentId,
                "like" to -1
            )
            db.collection("likes").document(user!!.uid + ":" + comments[position].commentId)
                .set(newLike)
                .addOnSuccessListener {
                    likeImage.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                    dislikeImage.setImageResource(R.drawable.baseline_arrow_drop_down_red_24)
                    db.collection("likes")
                        .whereEqualTo("commentId", comments[position].commentId)
                        .get()
                        .addOnSuccessListener { documents ->
                            var likes = 0
                            for (document in documents) {
                                likes += document.data["like"].toString().toInt()
                            }
                            likesCounter.text = likes.toString()
                        }
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error writing document")
                }
        }

        db.collection("users").document(comments[position].uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userTextView.text = document.data!!["email"].toString()
                }
            }
            .addOnFailureListener { exception ->
                userTextView.text = "Error"
            }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}