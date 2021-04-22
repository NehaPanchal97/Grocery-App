package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.databinding.CategotyListItemBinding
import com.grocery.app.models.Category

class CategoryListAdapter(private val categories: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryItem>() {


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


    inner class CategoryItem(private val binder: CategotyListItemBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun bind(category: Category) {
            binder.name = category.name
            binder.url = category.url
            binder.rank = "Rank: ${category.rank}"
            binder.placeholder = R.drawable.category_placeholder
            binder.executePendingBindings()
        }
    }
}