package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.databinding.FragmentChatBinding
import com.cagudeloa.unifavores.model.Chat
import com.cagudeloa.unifavores.model.User
import com.cagudeloa.unifavores.ui.home.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_home.*

class ChatFragment : Fragment() {

    private lateinit var userId: String
    private lateinit var binding: FragmentChatBinding
    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            userId = it.getString("user")!!
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        databaseReference!!.addValueEventListener(object : ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.getValue(User::class.java)
             (activity as AppCompatActivity).supportActionBar?.title = user!!.username
         }

         override fun onCancelled(error: DatabaseError) {

         }

        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        readMessage(firebaseUser!!.uid, userId)
        binding.sendMessageButton.setOnClickListener{
            val message = binding.messageEdit.text.toString()
            if(message.isNotEmpty()){
                sendMessage(firebaseUser!!.uid, userId, message)
                binding.messageEdit.setText("")
            }
        }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String){
        val databaseReference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        val hashMap: HashMap<String, String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        databaseReference!!.child("Chat").push().setValue(hashMap)
    }

    private fun readMessage(senderId: String, receiverId: String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Chat")
        databaseReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapshot: DataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if(chat!!.senderId == senderId && chat.receiverId == receiverId ||
                        chat.senderId == receiverId && chat.receiverId == senderId) {
                        chatList.add(chat)
                    }
                }

                // Setup adapter
                if(isAdded){
                    val chatAdapter = ChatAdapter(requireContext(), chatList)
                    binding.chatRecyclerView.adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}