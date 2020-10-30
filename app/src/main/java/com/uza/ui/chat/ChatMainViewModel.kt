package com.uza.ui.chat

import androidx.lifecycle.ViewModel
import com.uza.data.models.ChatRoom

class ChatMainViewModel : ViewModel() {
    val chats: ArrayList<ChatRoom> = ArrayList()
}