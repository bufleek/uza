package com.uza.data.repositories

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uza.data.models.Message

class ChatRepo(chatRoomId: String) {
    private val database = Firebase.database
    private val chatDatabaseReference: DatabaseReference

    init {
        chatDatabaseReference = database.getReference("messages/${chatRoomId}")
    }


    fun sendMessage(newMessage: Message) {
        chatDatabaseReference.push().setValue(newMessage)
    }

}