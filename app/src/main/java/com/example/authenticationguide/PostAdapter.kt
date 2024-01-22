package com.example.authenticationguide

import android.app.AlertDialog
import android.content.Intent
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

class PostAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth
    val user = auth.currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post_uid: TextView = holder.itemView.findViewById(R.id.post_uid_textView)
        val post_text: TextView = holder.itemView.findViewById(R.id.post_text_textView)
        val deleteImageView: ImageView = holder.itemView.findViewById(R.id.delete_imageView)
        val followImage: ImageView = holder.itemView.findViewById(R.id.follow_imageView)

        post_uid.text = posts[position].uid
        post_text.text = posts[position].text

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, SinglePostActivity::class.java)
            intent.putExtra("postId", posts[position].postId)
            holder.itemView.context.startActivity(intent)
        }

        val docRef = db.collection("follows")
        .document(user!!.uid + ":" + posts[position].postId)
        docRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                followImage.setImageResource(R.drawable.baseline_favorite_24)
                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
            } else {
                Log.d("TAG", "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }

        //following posts
        followImage.setOnClickListener {
            db.collection("follows").document(user!!.uid + ":" + posts[position].postId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        db.collection("follows").document(user!!.uid + ":" + posts[position].postId)
                            .delete()
                            .addOnSuccessListener {
                                followImage.setImageResource(R.drawable.baseline_favorite_border_24)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    } else {
                        val newFollow = hashMapOf(
                            "uid" to user!!.uid,
                            "postId" to posts[position].postId
                        )
                        db.collection("follows").document(user!!.uid + ":" + posts[position].postId)
                            .set(newFollow)
                            .addOnSuccessListener {
                                followImage.setImageResource(R.drawable.baseline_favorite_24)
                            }
                            .addOnFailureListener { e -> Log.w("Follow", "Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }


        }


        //deleting posts
        if(user!!.uid != posts[position].uid)
        {
            deleteImageView.visibility = View.INVISIBLE
        }
        deleteImageView.setOnClickListener {
            if(user!!.uid == posts[position].uid)
            {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Delete")
                    .setMessage("Are you shure you want to delete this post?")
                    .setPositiveButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }.setNegativeButton("Delete") { dialog, which ->
                        db.collection("posts").document(posts[position].postId)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully deleted!")
                                posts.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, posts.size)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    }.show()


            }

        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}