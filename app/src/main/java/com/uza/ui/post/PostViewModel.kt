package com.uza.ui.post

import android.graphics.Bitmap
import android.text.Editable
import android.widget.CompoundButton
import androidx.lifecycle.ViewModel
import com.uza.data.models.PostItem
import com.uza.data.repositories.PostsRepo

class PostViewModel : ViewModel() {
    private val postsRepo = PostsRepo()
    var postItem: PostItem = PostItem()
    val pickedImages: ArrayList<Bitmap> = ArrayList()

    fun setTitle(editable: Editable){
        postItem.title = editable.toString()
    }
    fun setDescription(editable: Editable){
        postItem.description = editable.toString()
    }
    fun setPrice(editable: Editable){
        postItem.price = editable.toString()
    }

    fun setAvailability(compoundButton: CompoundButton, boolean: Boolean){
        postItem.isAvailable = boolean
    }

    fun uploadPost(uid: String) {
        postsRepo.uploadPost(uid, postItem, pickedImages)
    }
}