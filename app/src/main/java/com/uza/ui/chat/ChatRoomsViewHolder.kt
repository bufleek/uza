package com.uza.ui.chat

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.uza.data.models.Message
import kotlinx.android.synthetic.main.chat_room_item.view.*

class ChatRoomsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, ChatActivity::class.java))
        }
    }
}
