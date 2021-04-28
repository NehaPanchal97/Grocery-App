package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.databinding.CategotyListItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Category

class CategoryListAdapter(val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryItem>() {

    var onClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItem {
        val binder =
            CategotyListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryItem(binder)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryItem, position: Int) {
        holder.bind(categories[position])
    }

    fun updateAdapter(arrayList: java.util.ArrayList<Category>) {
        categories.addAll(arrayList)
        notifyDataSetChanged()
    }
    fun clearAdapter(){
        categories.clear()
        notifyDataSetChanged()
    }


    inner class CategoryItem(private val binder: CategotyListItemBinding) :
        RecyclerView.ViewHolder(binder.root), View.OnClickListener {

        fun bind(category: Category) {
            binder.name = category.name
            binder.url = category.url
            binder.rank = "Rank: ${category.rank}"
            binder.placeholder = R.drawable.category_placeholder
            binder.editIv.setOnClickListener(this)
            binder.executePendingBindings()
        }

        override fun onClick(v: View?) {
            onClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
        }
    }
}