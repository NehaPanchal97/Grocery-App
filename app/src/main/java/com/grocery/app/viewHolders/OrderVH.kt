package com.grocery.app.viewHolders

import com.grocery.app.R
import com.grocery.app.databinding.AdminOrderListItemBinding
import com.grocery.app.models.Order

class OrderVH(private val binder: AdminOrderListItemBinding) :
    BaseVH<AdminOrderListItemBinding, Order>(binder) {

    override fun bind(data: Order) {

        val itemCount = data.items?.size ?: 0
        binder.name = data.name
        binder.address = data.deliveryAddress
        binder.placeholder = R.drawable.category_placeholder
        binder.itemCount = "$itemCount Items"
        binder.url = data.items?.firstOrNull()?.url
        binder.status = data.currentStatus
        binder.executePendingBindings()
    }
}