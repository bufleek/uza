package com.uza.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uza.R
import com.uza.data.models.Message

class ChatAdapter: RecyclerView.Adapter<ChatViewHolder>() {
    private var messages: ArrayList<Message> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_sender_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun clearData(){
        messages.clear()
    }

    fun changeData(messages: ArrayList<Message>){
        this.messages = messages
        notifyDataSetChanged()
    }

    fun addMessage(message: Message){
        messages.add(message)
        notifyDataSetChanged()
    }
}