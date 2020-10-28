package com.uza.data.repositories

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uza.data.models.ChatRoom

class ChatsRepo {
    private val storage = Firebase.database
    private val chatRoomsReference = storage.getReference("chatrooms")
    private var chatRooms: ArrayList<ChatRoom> = ArrayList()
    private val chatRoomsListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            if (snapshot.exists()) {
                val chatRoom = snapshot.getValue(ChatRoom::class.java)
                chatRoom?.let {
                    it.id = snapshot.key
                    chatRooms.add(it)
                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            resetChatRooms()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            resetChatRooms()
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            resetChatRooms()
        }

        override fun onCancelled(error: DatabaseError) {
            resetChatRooms()
        }

    }

    fun resetChatRooms() {
        chatRooms.clear()
        chatRoomsReference.removeEventListener(chatRoomsListener)
        chatRoomsReference.addChildEventListener(chatRoomsListener)
    }

    fun getChatRooms() {
        chatRoomsReference.addChildEventListener(chatRoomsListener)
    }
}