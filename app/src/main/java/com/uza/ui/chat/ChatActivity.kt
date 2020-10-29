package com.uza.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
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
        val user = Firebase.auth.currentUser
        if (user == null){
            Toast.makeText(baseContext, getString(R.string.login_error_occured), Toast.LENGTH_LONG).show()
            finish()
            return
        }
        else{
            viewModel.currentUserId = user.uid
            chatAdapter = ChatAdapter(viewModel.currentUserId!!)
        }
        val intent = intent
        if (intent != null){
            val chatRoom = intent.getStringExtra("chatroom")
            if (chatRoom == null || chatRoom.isEmpty()){
                Toast.makeText(baseContext, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                finish()
            }
            else{
                viewModel.chatRoomId = chatRoom
                viewModel.initialize(chatRoom)
            }
        }
        else{
            Toast.makeText(baseContext, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            finish()
        }
        val binding: ActivityChatBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setSupportActionBar(toolbar)
        back.setOnClickListener { finish() }

        chatDatabaseReference = database.getReference("messages/${viewModel.chatRoomId}")
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
                        recyclerView.scrollToPosition(viewModel.chatMessages.size - 1)
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