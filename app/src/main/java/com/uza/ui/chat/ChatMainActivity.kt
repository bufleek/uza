package com.uza.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.uza.R
import kotlinx.android.synthetic.main.activity_chat_main.*

class ChatMainActivity : AppCompatActivity() {
    private val chatRoomsAdapter = ChatRoomsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        val linearLayoutManager = LinearLayoutManager(this)
        chatsRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = chatRoomsAdapter
        }

    }
}