package com.uza.ui.chat

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.uza.data.models.ChatRoom
import com.uza.data.models.Message
import kotlinx.android.synthetic.main.chat_room_item.view.*

class ChatRoomsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var chatWithName: String? = null

    init {
        itemView.visibility = View.GONE
    }

    fun bind(
        chatReference: DatabaseReference,
        chatRoom: ChatRoom,
        chatWithReference: DatabaseReference
    ) {
        itemView.setOnClickListener {
            val intent = Intent(itemView.context, ChatActivity::class.java)
            intent.putExtra("chatroom", chatRoom.id)
            intent.putExtra("chatWith", chatRoom.chatWith)
            itemView.context.startActivity(Intent(intent))
        }
        chatReference.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    for (childSnapshot: DataSnapshot in snapshot.children) {
                        val message = childSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            itemView.chatLastMessage.text = message.text
                            itemView.date.text = message.date
                            chatWithReference.child("name")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            chatWithName = snapshot.getValue(String::class.java)
                                            itemView.userName.text = chatWithName
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}

                                })
                        }
                    }
                }
                itemView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
}
