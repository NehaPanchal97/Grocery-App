package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

class OrderDescriptionItemVH (private val binder: OrderDescriptionItemBinding):
    BaseVH<OrderDescriptionItemBinding, Product>(binder){



    override fun bind(data: Product) {

        binder.orderDesItemTitle.text = data.name
        binder.orderDesItemPrice.text = data.price.toString()
        binder.orderDesItemImage.loadImage(url = data.url)

    }


}