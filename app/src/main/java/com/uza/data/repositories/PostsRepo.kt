package com.uza.data.repositories

import android.graphics.Bitmap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.uza.data.models.PostItem
import java.io.ByteArrayOutputStream

class PostsRepo {
    private val database = Firebase.database
    private val storage = Firebase.storage.reference
    private val postsDbRef: DatabaseReference

    init {
        database.setPersistenceEnabled(true)
        postsDbRef = database.getReference("posts")
    }

    fun uploadPost(postItem: PostItem, pickedImages: ArrayList<Bitmap>) {
        val timestamp = System.currentTimeMillis()
        if (postItem.id == null) {
            postItem.dateCreated = timestamp
        }
        postItem.lastModified = timestamp
        val imagesRef = if (postItem.id == null) {
            timestamp
        } else {
            postItem.dateCreated
        }
        for (i in 0 until pickedImages.size) {
            val imageRef = "${imagesRef}_${i}"
            val baos = ByteArrayOutputStream()
            pickedImages[i].compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            storage.child("posts/${imageRef}").putBytes(data)
            postItem.images.add("posts/${imageRef}")
        }
        //create new post
        if (postItem.id == null) postsDbRef.push().setValue(postItem)
        //edit existing post
        else postsDbRef.child(postItem.id!!).setValue(postItem)
    }
}