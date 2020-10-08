package com.cagudeloa.unifavores.ui.incompleteFavors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cagudeloa.unifavores.R
import com.cagudeloa.unifavores.base.BaseViewHolder
import com.cagudeloa.unifavores.model.Favor
import kotlinx.android.synthetic.main.incomplete_favor_item.view.*

class IncompleteFavorsAdapter(
    private val context: Context,
    private val itemClickListener: IncompleteFavorsAdapter.OnItemClickListener,
    private val favor: ArrayList<Favor>
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnItemClickListener {
        fun onItemClick(favor: Favor)
    }

    inner class IncompleteFavorViewHolder(itemView: View) : BaseViewHolder<Favor>(itemView) {
        override fun bind(item: Favor, position: Int) {
            itemView.favorTitleText.text = item.favorTitle
            itemView.favorDescriptionText.text = item.favorDescription
            itemView.setOnClickListener { itemClickListener.onItemClick(item) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return IncompleteFavorViewHolder(
            LayoutInflater.from(context).inflate(R.layout.incomplete_favor_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is IncompleteFavorViewHolder)
            holder.bind(favor[position], position)
    }

    override fun getItemCount(): Int {
        return favor.size
    }
}