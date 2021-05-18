package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

class OrderDescriptionItemVH (private val binder: OrderDescriptionItemBinding):
    BaseVH<OrderDescriptionItemBinding, Product>(binder){



    override fun bind(data: Product) {

        binder.cartItemTitle.text = data.name
        binder.cartItemPrice.text = data.price.toString()
        binder.cartItemImage.loadImage(data.url)

    }


}