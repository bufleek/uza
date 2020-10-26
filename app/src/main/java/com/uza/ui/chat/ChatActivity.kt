package com.uza.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.uza.R
import com.uza.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewGroup: ViewGroup = findViewById(android.R.id.content)
        val binding: ActivityChatBinding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        
    }
}