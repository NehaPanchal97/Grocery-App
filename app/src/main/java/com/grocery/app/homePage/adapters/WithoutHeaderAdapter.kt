package com.grocery.app.homePage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.homePage.dataModel.ItemData
import com.grocery.app.R
import com.grocery.app.models.Category


//Adapter for 1 screen 2 row
class WithoutHeaderAdapter(private val itemList: ArrayList<Category>?) :
    RecyclerView.Adapter<WithoutHeaderAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_without_header, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        Glide.with(myViewHolder.itemView.context).load(itemList?.get(position)?.url)
            .into(myViewHolder.img_item)

    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    @Suppress("DEPRECATION")
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var img_item: ImageView

        init {

            img_item = view.findViewById(R.id.cardview_image) as ImageView

        }

    }
}