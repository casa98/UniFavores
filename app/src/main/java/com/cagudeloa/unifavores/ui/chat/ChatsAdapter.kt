package com.cagudeloa.unifavores.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.base.BaseViewHolder
import com.cagudeloa.unifavores.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *
 * This score is being used for two fragments
 * - Statistics
 * - Chats
 *
 * That's why you'll see a couple of maybe unrecommended tricks :c
 */
class ChatsAdapter(
    private val context: Context,
    private val itemClickListener: ChatsAdapter.OnItemClickListener,
    private val chatList: List<User>,
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    inner class ChatsViewHolder(itemView: View) : BaseViewHolder<User>(itemView) {
        @SuppressLint("SetTextI18n")
        override fun bind(item: User, position: Int) {
            itemView.chatUsernameText.text = item.username
            if (item.score.toString() == "-1") {
                // Comes from ChatsFragment and..
                // It's a favor that someone is making to the currentUser
                itemView.chatSecondText.text = "Me está haciendo un favor"
            } else if (item.score.toString() == "-2") {
                // Comes from ChatsFragment and..
                // It's a favor that currentUser is making to someone else
                itemView.chatSecondText.text = "Le estoy haciendo un favor"
            } else {
                // Comes from StatisticsFragment, show everything
                itemView.chatSecondText.text = item.score.toString() + " puntos"
            }
            //itemView.chatUserImage.setImageResource(R.drawable.someone)
            if (item.image.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    Picasso.get().load(item.image).placeholder(R.drawable.loading)
                        .error(R.drawable.ic_person)
                        .into(itemView.chatUserImage)
                }
            } else {
                itemView.chatUserImage.setImageResource(R.drawable.ic_big_person)
            }
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