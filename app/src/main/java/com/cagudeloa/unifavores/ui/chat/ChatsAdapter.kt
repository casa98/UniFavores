package com.cagudeloa.unifavores.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.base.BaseViewHolder
import com.cagudeloa.unifavores.model.User
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatsAdapter(
    private val context: Context,
    private val itemClickListener: ChatsAdapter.OnItemClickListener,
    private val chatList: ArrayList<User>,
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    inner class ChatsViewHolder(itemView: View) : BaseViewHolder<User>(itemView) {
        override fun bind(item: User, position: Int) {
            itemView.chatUsernameText.text = item.username
            itemView.chatSecondText.text = item.uid
            itemView.chatUserImage.setImageResource(R.drawable.someone)
            itemView.setOnClickListener { itemClickListener.onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ChatsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is ChatsViewHolder)
            holder.bind(chatList[position], position)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}