package com.cagudeloa.unifavores.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context,
                  private val chatList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val messageTypeLeft = 0
    private val messageTypeRight = 1
    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val chatImage: CircleImageView = view.findViewById(R.id.chatImage)
        val chatMessage: TextView = view.findViewById(R.id.chatMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == messageTypeRight){
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        // TODO Load image using Picasso :)
        holder.chatMessage.text = chat.message
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(chatList[position].senderId == firebaseUser!!.uid){
            return messageTypeRight
        }else{
            return messageTypeLeft
        }
    }


}