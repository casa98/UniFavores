package com.cagudeloa.unifavores.ui.myfavors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.base.BaseViewHolder
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.my_favor_item.view.*

class MyFavorsAdapter(
    private val context: Context,
    private val itemClickListener: MyFavorsFragment,
    private val favor: ArrayList<Favor>
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnItemClickListener {
        fun onItemClick(favor: Favor)
    }

    inner class MyFavorViewHolder(itemView: View) : BaseViewHolder<Favor>(itemView) {
        override fun bind(item: Favor, position: Int) {
            itemView.favorCreationText.text = item.creationDate
            itemView.favorTitleText.text = item.favorTitle
            itemView.favorDescriptionText.text = item.favorDescription
            itemView.favorLocationtext.text = item.favorLocation
            itemView.statusText.text = item.status  // TODO It display number but I want text
            itemView.setOnClickListener { itemClickListener.onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MyFavorViewHolder(
            LayoutInflater.from(context).inflate(R.layout.my_favor_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is MyFavorsAdapter.MyFavorViewHolder)
            holder.bind(favor[position], position)
    }

    override fun getItemCount(): Int {
        return favor.size
    }
}