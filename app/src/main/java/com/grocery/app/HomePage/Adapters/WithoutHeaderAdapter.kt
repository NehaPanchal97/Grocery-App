package com.grocery.app.HomePage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.R

class WithoutHeaderAdapter(private val context: Context,
                             private val itemList:ArrayList<ItemData>?):
    RecyclerView.Adapter<WithoutHeaderAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cardview_without_header,p0,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {

        Glide.with(context).load(itemList?.get(position)?.image).into(myViewHolder.img_item)

    }

    override fun getItemCount(): Int {
        return itemList?.size?:0
    }

    @Suppress("DEPRECATION")
    inner class MyViewHolder(view : View): RecyclerView.ViewHolder(view){

        var img_item: ImageView

        init {

            img_item = view.findViewById(R.id.cardview_image) as ImageView

        }

    }
}