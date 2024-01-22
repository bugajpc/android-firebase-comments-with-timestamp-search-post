package com.example.authenticationguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommentAdapter(val comments: MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    val db = Firebase.firestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val dateTextView: TextView = holder.itemView.findViewById(R.id.date_textView)
        val commentTextView: TextView = holder.itemView.findViewById(R.id.comment_text_textView)
        val userTextView: TextView = holder.itemView.findViewById(R.id.comment_user_textView)

        dateTextView.text = comments[position].timestamp.toDate().toString()
        commentTextView.text = comments[position].text
        //userTextView.text = comments[position].uid

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