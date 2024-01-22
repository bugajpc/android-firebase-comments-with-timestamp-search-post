package com.example.authenticationguide

data class Comment(val commentId: String, val uid: String, val postId: String, val text: String, val timestamp: com.google.firebase.Timestamp)