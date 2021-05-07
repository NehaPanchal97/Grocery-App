package com.grocery.app.viewHolders

import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

 class CartItemVH(private val binder: CartItemBinding):
        BaseVH<CartItemBinding, Product>(binder){
    override fun bind(data: Product) {
        binder.cartItemTitle.text = data.name
        binder.cartItemPrice.text = data.price.toString()
        binder.cartItemImage.loadImage(url = data.url)

    }

}