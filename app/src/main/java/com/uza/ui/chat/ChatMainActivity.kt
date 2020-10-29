package com.uza.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.uza.R
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

        chatsListener = object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                connecting.visibility = View.INVISIBLE
                if(snapshot.exists()){
                    val chat = snapshot.getValue(String::class.java)
                    if (chat != null){
                        noChats.visibility = View.GONE
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

    override fun onResume() {
        super.onResume()
        val user = auth.currentUser
        if (user == null || user != this.user){
            Toast.makeText(this, getString(R.string.login_error_occured), Toast.LENGTH_LONG).show()
            finish()
        }else{
            connecting.visibility = View.VISIBLE
            chatRoomsAdapter.changeData(viewModel.chats)
            viewModel.chats.clear()
            chatRoomsAdapter.clearData()
            dbRef.limitToLast(30).addChildEventListener(chatsListener)
            dbRef.limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChildren()){
                        if (viewModel.chats.size < 1){
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