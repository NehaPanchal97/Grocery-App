package com.grocery.app.homePage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.homePage.dataModel.ItemData
import com.grocery.app.R


//Adapter for 3 screen
class SpecificItemAdapter(private val context: Context,
                          private val itemList:ArrayList<ItemData>): RecyclerView.Adapter<SpecificItemAdapter.GridViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.specific_item_with_price,parent,false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val items = itemList.get(position)

        holder.txt_title.text=items.name
        holder.txt_amount.text=items.amount
        holder.txt_price.text=items.price
        Glide.with(context).load(itemList[position].image).into(holder.img_item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class GridViewHolder(view: View): RecyclerView.ViewHolder(view) {

        var txt_title: TextView = view.findViewById(R.id.tvTitle) as TextView
        var txt_amount:TextView = view.findViewById(R.id.tv_amount) as TextView
        var txt_price:TextView = view.findViewById(R.id.tv_price) as TextView
        var img_item: ImageView = view.findViewById(R.id.itemImage) as ImageView
    }
}