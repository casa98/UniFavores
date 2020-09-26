package com.cagudeloa.unifavores.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.User
import kotlinx.android.synthetic.main.item_recycler.view.*

class UserAdapter(private val context: Context, private val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.ViewHolder> (){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val username: TextView = view.usernameText
        val image: ImageView = view.userImage
        val text: TextView = view.secondText
        // TODO Set here layout for future OnClickListener on items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.username
        holder.image.setImageResource(R.drawable.ic_person)
        holder.text.text = user.message
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}