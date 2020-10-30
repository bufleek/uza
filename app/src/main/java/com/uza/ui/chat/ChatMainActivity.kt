package com.uza.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uza.R
import com.uza.data.models.ChatRoom
import kotlinx.android.synthetic.main.activity_chat_main.*

class ChatMainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ChatMainViewModel
    private var user: FirebaseUser? = null
    private val database = Firebase.database
    private val chatRoomsAdapter = ChatRoomsAdapter(database)
    private lateinit var dbRef: DatabaseReference
    private lateinit var chatsListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        auth = Firebase.auth
        user = auth.currentUser
        dbRef = database.getReference("users/${user?.uid}/chatrooms")
        viewModel = ViewModelProvider(this).get(ChatMainViewModel::class.java)
        val linearLayoutManager = LinearLayoutManager(this)
        chatsRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = chatRoomsAdapter
        }

        val intent = intent
        val chatWith = intent.getStringExtra("chatWith")
        if (!chatWith.isNullOrEmpty()) {
            val alertDialog = AlertDialog.Builder(this)

            val title = TextView(this)
            title.text = getString(R.string.chat_with_seller)
            title.setPadding(10, 10, 10, 10)
            title.gravity = Gravity.CENTER
            title.textSize = 18f

            val message = TextView(this)
            message.text = getString(R.string.preparing_chat)
            message.setPadding(10, 10, 10, 10)
            message.gravity = Gravity.CENTER
            message.textSize = 16f


            alertDialog.setCustomTitle(title)
            alertDialog.setView(message)
            val dialog = alertDialog.create()
            dialog.show()
            dialog.setCancelable(false)
            if (user != null) {
                val possibleChatId1 = "${user?.uid}_${chatWith}"
                val possibleChatId2 = "${chatWith}_${user?.uid}"
                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var chatExists = false
                        for (snap: DataSnapshot in snapshot.children) {
                            val chat = snap.getValue(ChatRoom::class.java)
                            if (chat != null && chat.id == possibleChatId1 || chat != null && chat.id == possibleChatId2) {
                                dialog.dismiss()
                                snap.key?.let { launchPreparedChatRoom(it, chatWith) }
                                chatExists = true
                                break
                            }
                        }
                        if (!chatExists) {
                            //create new chatroom
                            dbRef.child(possibleChatId1).child("chatWith").setValue(chatWith)
                            database.getReference("users/${chatWith}/chatrooms")
                                .child(possibleChatId1).child("chatWith").setValue(user?.uid)
                            dialog.dismiss()
                            launchPreparedChatRoom(possibleChatId1, chatWith)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        dialog.dismiss()
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_to_prepare_chat),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
            } else {
                dialog.dismiss()
            }
        }
        chatsListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                connecting.visibility = View.INVISIBLE
                if (snapshot.exists()) {
                    val chat = snapshot.getValue(ChatRoom::class.java)
                    if (chat != null) {
                        noChats.visibility = View.GONE
                        chat.id = snapshot.key
                        viewModel.chats.add(chat)
                        chatRoomsAdapter.changeData(viewModel.chats)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, error.message, Toast.LENGTH_LONG).show()
            }

        }
    }

    fun launchPreparedChatRoom(chat: String, chatWith: String) {
        startActivity(
            Intent(this, ChatActivity::class.java)
                .putExtra("chatroom", chat)
                .putExtra("chatWith", chatWith)
        )
    }

    override fun onResume() {
        super.onResume()
        val user = auth.currentUser
        if (user == null || user != this.user) {
            Toast.makeText(this, getString(R.string.login_error_occured), Toast.LENGTH_LONG).show()
            finish()
        } else {
            connecting.visibility = View.VISIBLE
            chatRoomsAdapter.changeData(viewModel.chats)
            viewModel.chats.clear()
            chatRoomsAdapter.clearData()
            dbRef.limitToLast(30).addChildEventListener(chatsListener)
            dbRef.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChildren()) {
                        if (viewModel.chats.size < 1) {
                            noChats.visibility = View.VISIBLE
                            connecting.visibility = View.INVISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }
    }

    override fun onPause() {
        super.onPause()
        dbRef.removeEventListener(chatsListener)
    }
}