package com.uza.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.uza.R

class ChatRoomsAdapter: RecyclerView.Adapter<ChatRoomsViewHolder>() {
    private var chats: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_room_item, parent, false)
        return ChatRoomsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomsViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun clearData(){
        chats.clear()
    }

    fun changeData(chats: ArrayList<String>){
        this.chats = chats
        notifyDataSetChanged()
    }
}