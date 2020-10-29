package com.uza.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.uza.models.Post

class ShopsViewModel: ViewModel() {
    private val ref = Firebase.database.getReference("posts")
    private val storage = FirebaseStorage.getInstance().reference
    val posts: MutableLiveData<List<Post>> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getPosts(){
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = mutableListOf<Post>()
                for (post in snapshot.children)
                    data.add(post.getValue(Post::class.java)!!)
                posts.value = data
            }

            override fun onCancelled(e: DatabaseError) {
                error.value = e.message
            }
        })
    }
}