package com.cagudeloa.unifavores.ui.favors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.item_recycler.view.*

class FavorsAdapter(private val context: Context, private val favor: ArrayList<Favor>): RecyclerView.Adapter<FavorsAdapter.ViewHolder> (){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val user: TextView = view.usernameText
        val image: ImageView = view.userImage
        val text: TextView = view.secondText
        // TODO Set here layout for future OnClickListener on items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favor = favor[position]
        holder.user.text = favor.user
        holder.image.setImageResource(R.drawable.ic_person)
        holder.text.text = favor.favorTitle
    }

    override fun getItemCount(): Int {
        return favor.size
    }

}