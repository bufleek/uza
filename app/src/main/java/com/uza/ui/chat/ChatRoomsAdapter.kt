package com.uza.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.uza.R
import com.uza.data.models.ChatRoom

class ChatRoomsAdapter(private val database: FirebaseDatabase) :
    RecyclerView.Adapter<ChatRoomsViewHolder>() {
    private var chats: ArrayList<ChatRoom> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_room_item, parent, false)
        return ChatRoomsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomsViewHolder, position: Int) {
        val chatReference: DatabaseReference =
            database.getReference("messages/${chats[position].id}")
        val chatWithReference: DatabaseReference =
            database.getReference("users/${chats[position].chatWith}")
        holder.bind(chatReference, chats[position], chatWithReference)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun clearData() {
        chats.clear()
    }

    fun changeData(chats: ArrayList<ChatRoom>) {
        this.chats = chats
        notifyDataSetChanged()
    }
}