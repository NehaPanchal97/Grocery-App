package com.grocery.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.databinding.AdminOrderListItemBinding
import com.grocery.app.models.Order
import com.grocery.app.viewHolders.OrderVH

class OrderListAdapter(private val orders: ArrayList<Order>) : RecyclerView.Adapter<OrderVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val binder =
            AdminOrderListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderVH(binder)
    }

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun update(data: ArrayList<Order>) {
        orders.clear()
        orders.addAll(data)
        notifyDataSetChanged()
    }
}