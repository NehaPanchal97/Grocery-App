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
import com.grocery.app.extensions.disable
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Product
import kotlinx.android.synthetic.main.specific_item_with_price.view.*


//Adapter for 3 screen
class SpecificItemAdapter(
    private val itemList: ArrayList<Product>,
    private val cartMap: HashMap<String, Product?> = hashMapOf()
) :
    RecyclerView.Adapter<SpecificItemAdapter.GridViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    val items
        get() = itemList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.specific_item_with_price, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val items = itemList.get(position)

        holder.txt_title.text = items.name
//        holder.txt_amount.text=items.amount
        holder.txt_price.text = items.price.toString()
        Glide.with(holder.itemView.context).load(itemList[position].url).into(holder.img_item)
        val pid = itemList[position].id ?: ""
        val cartCount = cartMap[pid]?.count ?: 0
        holder.itemView.tv_count.text = "$cartCount"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var txt_title: TextView = view.findViewById(R.id.specificItemTitle) as TextView

        //        var txt_amount:TextView = view.findViewById(R.id.tv_amount) as TextView
        var txt_price: TextView = view.findViewById(R.id.tv_price) as TextView
        var img_item: ImageView = view.findViewById(R.id.itemImage) as ImageView

        init {
            itemView.iv_add.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.disable(300)
            itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
        }

    }

    fun update(arrayList: java.util.ArrayList<Product>) {
        itemList.addAll(arrayList)
        notifyDataSetChanged()
    }
}