package com.uza.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.uza.models.Posts

class ShopsViewModel: ViewModel() {
    private val ref = Firebase.database.getReference("posts")
    val posts: MutableLiveData<Posts> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    fun getPosts(){
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Posts", "onDataChange: ${snapshot.value}")
                posts.value = snapshot.getValue<Posts>()
            }

            override fun onCancelled(e: DatabaseError) {
                error.value = e.message
            }
        })
    }
}