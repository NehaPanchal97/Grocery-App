package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.TagListItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.viewHolders.BaseVH

class TagListAdapter(private var tags: ArrayList<String?>? = null) :
    RecyclerView.Adapter<TagListAdapter.TagVH>() {

    val tagList
        get() = tags

    var itemClickListener: OnItemClickListener? = null


    inner class TagVH(private val binder: TagListItemBinding) :
        BaseVH<TagListItemBinding, String>(binder), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(data: String) {
            binder.tag = data
            binder.executePendingBindings()
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagVH {
        val binder = TagListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TagVH(binder).apply { itemClickListener = this@TagListAdapter.itemClickListener }
    }

    override fun onBindViewHolder(holder: TagVH, position: Int) {
        val tag = tags?.getOrNull(position)
        holder.bind(tag ?: "")
    }

    override fun getItemCount(): Int {
        return tags?.size ?: 0
    }

    fun update(data: ArrayList<String?>?) {
        tags = data
        notifyDataSetChanged()
    }
}