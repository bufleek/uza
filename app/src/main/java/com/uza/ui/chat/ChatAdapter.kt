package com.uza.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uza.R
import com.uza.data.models.Message

class ChatAdapter(private val uid: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val senderType:Int = 1
    private val receiverType:Int = 2
    private var messages: ArrayList<Message> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = if (viewType == senderType){
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_sender_message, parent, false)
        }else{
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_receiver_message, parent, false)
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatViewHolder).bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == uid){
            senderType
        }else{
            receiverType
        }
    }

    fun clearData(){
        messages.clear()
    }

    fun changeData(messages: ArrayList<Message>){
        this.messages = messages
        notifyDataSetChanged()
    }
}