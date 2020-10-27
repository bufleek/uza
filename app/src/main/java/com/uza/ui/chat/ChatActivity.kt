package com.uza.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uza.R
import com.uza.data.models.Message
import com.uza.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_sender_message.*

class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messagesListener: ChildEventListener
    private val database = Firebase.database
    private lateinit var chatDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        val binding: ActivityChatBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        chatDatabaseReference = database.getReference("messages/${viewModel.chatRoomId}")
        chatAdapter = ChatAdapter()
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = chatAdapter
        }
        sendMessage.setOnClickListener {
            viewModel.onSendClicked()
        }

        viewModel.isSendEnabled.observe(this) {
            sendMessage.isEnabled = it
        }

        viewModel.clearNewMessageEditText.observe(this) {
            if (it) {
                newMessage.setText("")
                sendMessage.isEnabled = false
            }
        }

        messagesListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.exists()) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null) {
                        message.id = snapshot.key
                        viewModel.chatMessages.add(message)
                        chatAdapter.changeData(viewModel.chatMessages)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "A connection error occurred", Toast.LENGTH_LONG).show()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.changeData(viewModel.chatMessages)
        viewModel.chatMessages.clear()
        chatAdapter.clearData()
        chatDatabaseReference.addChildEventListener(messagesListener)
    }

    override fun onPause() {
        super.onPause()
        chatDatabaseReference.removeEventListener(messagesListener)
    }
}