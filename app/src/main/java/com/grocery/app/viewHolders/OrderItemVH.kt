package com.grocery.app.viewHolders

import com.grocery.app.databinding.OrderItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

class OrderItemVH(
    private val binder: OrderItemBinding,
    private val cartMap: HashMap<String, Product?>
) : BaseVH<OrderItemBinding, Product>(binder) {

    override fun bind(data: Product) {
        binder.orderItemTitle.text = data.name
        binder.orderItemImage.loadImage(data.url)
        binder.orderItemPrice.text = data.price.toString()
    }
}