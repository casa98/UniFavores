package com.cagudeloa.unifavores.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.RetrofitInstance
import com.cagudeloa.unifavores.databinding.FragmentChatBinding
import com.cagudeloa.unifavores.model.Chat
import com.cagudeloa.unifavores.model.NotificationData
import com.cagudeloa.unifavores.model.PushNotification
import com.cagudeloa.unifavores.model.User
import com.cagudeloa.unifavores.ui.home.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var user: User
    private lateinit var binding: FragmentChatBinding
    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    private var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            user = it.getParcelable("user")!!
        }
        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)
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
        readMessage(firebaseUser!!.uid, user.uid)
        binding.sendMessageButton.setOnClickListener{
            val message = binding.messageEdit.text.toString()
            if(message.isNotEmpty()){
                sendMessage(firebaseUser!!.uid, user.uid, message)
                binding.messageEdit.setText("")
                /*
                topic  = "/topics/$user.uid"
                PushNotification(NotificationData(user.username, message), topic)
                    .also {notification ->
                        sendNotification( notification)
                    }
                 */
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
                    binding.apply {
                    chatRecyclerView.adapter = chatAdapter
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount-1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Toast.makeText(requireContext(), "Response: ${Gson().toJson(response)}", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(), response.errorBody().toString(), Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
    }

}