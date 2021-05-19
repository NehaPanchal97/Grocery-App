package com.grocery.app.homePage.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.OrderStatusItemBinding
import com.grocery.app.models.Order
import com.grocery.app.models.OrderStatus
import com.grocery.app.viewHolders.OrderStatusVH

class OrderStatusAdapter (private var orders: ArrayList<OrderStatus>) :
        RecyclerView.Adapter<OrderStatusVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.grocery.app.viewHolders.OrderStatusVH{
       val binder = OrderStatusItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderStatusVH(binder)
    }


    override fun onBindViewHolder(holder: OrderStatusVH, position: Int) {
       holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun update(data: ArrayList<OrderStatus>) {
        orders = data
        notifyDataSetChanged()
    }


}