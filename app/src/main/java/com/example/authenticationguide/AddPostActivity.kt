package com.example.authenticationguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddPostActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val textEditText: EditText = findViewById(R.id.postText_editText)
        val addPostButton: Button = findViewById(R.id.addPost_button)
        val db = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser

        addPostButton.setOnClickListener {
            val post = hashMapOf(
                "text" to textEditText.text.toString(),
                "uid" to user!!.uid
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener {
                    Log.d("AddPost", "DocumentSnapshot successfully written!")
                    Toast.makeText(
                        baseContext,
                        "Dodano post",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w("AddPost", "Error writing document", e) }

        }
    }
}