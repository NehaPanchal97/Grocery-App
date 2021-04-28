package com.grocery.app.HomePage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.R
import com.grocery.app.databinding.ProductListBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.viewHolders.BaseVH

// Adapter for 2 screen
class ProductItemsAdapter(private val context: Context,
                          private val itemList:ArrayList<ItemData>): RecyclerView.Adapter<ProductItemsAdapter.GridViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_list,parent,false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val items = itemList.get(position)

        holder.txt_title.text=items.name
        Glide.with(context).load(itemList[position].image).into(holder.img_item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class GridViewHolder(view:View): RecyclerView.ViewHolder(view) {

        var txt_title: TextView
        var img_item: ImageView
        init {
            txt_title = view.findViewById(R.id.tvTitle) as TextView
            img_item = view.findViewById(R.id.itemImage) as ImageView

        }
    }
}