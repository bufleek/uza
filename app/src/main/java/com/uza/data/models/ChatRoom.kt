package com.uza.data.models

data class ChatRoom(
    var id: String? = null,
    var messages: ArrayList<Message>? = null,
    var chatWith: String? = null
)
