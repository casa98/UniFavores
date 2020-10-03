package com.cagudeloa.unifavores.ui.favors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.base.BaseViewHolder
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.item_recycler.view.*

class FavorsAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener,
    private val favor: ArrayList<Favor>
): RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnItemClickListener{
        fun onItemClick(favor: Favor)
    }

    inner class FavorsViewHolder(itemView: View): BaseViewHolder<Favor>(itemView){
        override fun bind(item: Favor, position: Int) {
            itemView.favorTitle.text = item.favorTitle
            itemView.favorDescription.text = item.favorDescription
            itemView.setOnClickListener { itemClickListener.onItemClick(item) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return FavorsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if(holder is FavorsViewHolder)
            holder.bind(favor[position], position)
    }

    override fun getItemCount(): Int {
        return favor.size
    }

    /*
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val user: TextView = view.usernameText
        val text: TextView = view.secondText
        // TODO Set here layout for future OnClickListener on items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favor = favor[position]
        holder.user.text = favor.favorTitle
        holder.text.text = favor.favorDescription
    }

    override fun getItemCount(): Int {
        return favor.size
    }
    */
}