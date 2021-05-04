package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.homePage.dataModel.ItemData
import com.grocery.app.homePage.Interface.IItemClickListener
import com.grocery.app.R
import com.grocery.app.models.Category


//Adapter for 1 screen

class ProductListAdapter(private val itemList: ArrayList<Category>?) :
    RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        myViewHolder.txt_title.setText(itemList?.get(position)?.name)
        Glide.with(myViewHolder.itemView.context).load(itemList?.get(position)?.url)
            .into(myViewHolder.img_item)

        myViewHolder.setClick(object : IItemClickListener {
            override fun onItemClickListener(view: View, position: Int) {
//               Toast.makeText(context,""+itemList?.get(position)?.name,Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun getItemCount(): Int {
        return itemList?.size ?: 0
    }

    @Suppress("DEPRECATION")
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var txt_title: TextView = view.findViewById(R.id.tvTitle) as TextView
        var img_item: ImageView = view.findViewById(R.id.itemImage) as ImageView

        lateinit var iItemClickListener: IItemClickListener

        fun setClick(iItemClickListener: IItemClickListener) {
            this.iItemClickListener = iItemClickListener
        }


        init {

            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            iItemClickListener.onItemClickListener(view!!, adapterPosition)
        }

    }
}