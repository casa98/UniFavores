package com.cagudeloa.unifavores.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cagudeloa.unifavores.NODE_CHAT
import com.cagudeloa.unifavores.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessagesViewModel : ViewModel() {

    private var senderId = FirebaseAuth.getInstance().currentUser!!.uid
    private val _messages = MutableLiveData<ArrayList<Chat>>()
    val messages: LiveData<ArrayList<Chat>>
        get() = _messages


    fun readMessage(receiverId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference(NODE_CHAT)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = ArrayList<Chat>()
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapshot.getValue(Chat::class.java)!!
                    if (chat.senderId == senderId && chat.receiverId == receiverId ||
                        chat.senderId == receiverId && chat.receiverId == senderId
                    ) {
                        messages.add(chat)
                    }
                    _messages.value = messages
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendMessage(receiverId: String, message: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child(NODE_CHAT)
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        databaseReference.push().setValue(hashMap)
    }
}