package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.OrderItemBinding
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Order
import com.grocery.app.viewHolders.OrderItemVH

class OrderItemAdapter(private val orders:ArrayList<Order>): RecyclerView.Adapter<OrderItemVH>() {

    val items
        get() = orders

    var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemVH {
        val binder = OrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val holder =  OrderItemVH(binder)
        holder.itemClickListener = itemClickListener
        return holder
    }

    override fun onBindViewHolder(holder: OrderItemVH, position: Int) {
       holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }


    fun updateAdapterData(data: ArrayList<Order>?) {
        orders.addAll(data ?: arrayListOf())
        notifyDataSetChanged()
    }
}