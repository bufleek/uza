package com.uza.ui.chat


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.uza.data.models.Message
import kotlinx.android.synthetic.main.chat_sender_message.view.*

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: Message) {
        itemView.message.text = message.text
        itemView.dateStamp.text = message.date
    }
}