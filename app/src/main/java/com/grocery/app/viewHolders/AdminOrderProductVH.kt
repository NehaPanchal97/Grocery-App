package com.grocery.app.viewHolders

import com.grocery.app.R
import com.grocery.app.databinding.AdminOrderProductItemBinding
import com.grocery.app.models.Product

class AdminOrderProductVH(private val binder: AdminOrderProductItemBinding) :
    BaseVH<AdminOrderProductItemBinding, Product>(binder) {

    override fun bind(data: Product) {

        val res = itemView.context.resources
        val count = data.count ?: 0
        binder.url = data.url
        binder.placeholder = R.drawable.category_placeholder
        binder.count = "$count X"
        binder.name = data.name
        binder.price = res.getString(R.string.rs, data.price?.toString())
        binder.executePendingBindings()
    }
}