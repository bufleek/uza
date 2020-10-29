package com.uza.ui.chat

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uza.data.models.Message
import com.uza.data.repositories.ChatRepo
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatViewModel: ViewModel(){
    var currentUserId: String? = null
    var chatRoomId: String = ""
    private var chatRepo: ChatRepo? = null
    var newMessage: Message = Message()
    private val _isSendEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isSendEnabled: LiveData<Boolean>
        get() = _isSendEnabled
    val chatMessages: ArrayList<Message> = ArrayList()
    val clearNewMessageEditText: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

   fun initialize(room: String){
       chatRepo = ChatRepo(room)
   }

    fun setNewMessage(editable: Editable){
        newMessage.text = editable.toString().trim()
        if (newMessage.text.isNotEmpty()){
            _isSendEnabled.value = true
        }

    }

    fun onSendClicked(){
        if (newMessage.text.isEmpty()){
            _isSendEnabled.value = false
            return
        }
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        newMessage.date = date
        newMessage.sender = currentUserId
        if (!newMessage.sender.isNullOrEmpty() && chatRepo != null && chatRoomId.isNotEmpty()){
            chatRepo?.sendMessage(newMessage)
            newMessage = Message()
            clearNewMessageEditText.value = true
        }
    }
}