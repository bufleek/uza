package com.uza.ui.chat

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ChatRoomsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        itemView.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, ChatActivity::class.java))
        }
    }
}
