package com.grocery.app.HomePage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.HomePage.DataModel.ItemData
import com.grocery.app.HomePage.Interface.IItemClickListener
import com.grocery.app.R

class ChildHorizontalAdapter(private val context: Context,
                             private val itemList:ArrayList<ItemData>?):
    RecyclerView.Adapter<ChildHorizontalAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.layout_item,p0,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
       myViewHolder.txt_title.setText(itemList!![position].name)
        Glide.with(context).load(itemList[position].image).into(myViewHolder.img_item)

        myViewHolder.setClick(object : IItemClickListener {
            override fun onItemClickListener(view: View, position: Int) {
               Toast.makeText(context,""+itemList[position].name,Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun getItemCount(): Int {
        return itemList?.size?:0
    }

    @Suppress("DEPRECATION")
    inner class MyViewHolder(view : View):RecyclerView.ViewHolder(view),View.OnClickListener{

        var txt_title:TextView
        var img_item:ImageView

        lateinit var iItemClickListener:IItemClickListener

        fun setClick(iItemClickListener:IItemClickListener){
            this.iItemClickListener = iItemClickListener
        }


        init {
            txt_title = view.findViewById(R.id.tvTitle) as TextView
            img_item = view.findViewById(R.id.itemImage) as ImageView

            view.setOnClickListener(this)
        }
        override fun onClick(view: View?) {
            iItemClickListener.onItemClickListener(view!!,adapterPosition)
        }

    }
}