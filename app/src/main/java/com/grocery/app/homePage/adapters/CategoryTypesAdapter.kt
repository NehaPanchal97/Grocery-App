package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Category

// Adapter for 2 screen
class CategoryTypesAdapter(private val itemList: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryTypesAdapter.GridViewHolder>() {


    val items
        get() = itemList

    var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val items = itemList.get(position)

        holder.txt_title.text = items.name
        Glide.with(holder.itemView.context).load(itemList[position].url).into(holder.img_item)

        holder.img_item.setOnClickListener {
            itemClickListener?.onItemClick(it.id, position)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txt_title: TextView = view.findViewById(R.id.homePageTitle) as TextView
        var img_item: ImageView = view.findViewById(R.id.itemImage) as ImageView
    }

    fun updateCategory(data: ArrayList<Category>?) {
        val categories = data ?: arrayListOf()
        itemList.addAll(categories)
        notifyDataSetChanged()

    }


}