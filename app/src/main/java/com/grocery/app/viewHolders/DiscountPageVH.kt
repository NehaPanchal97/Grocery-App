package com.grocery.app.viewHolders

import com.grocery.app.R
import com.grocery.app.databinding.DiscountItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.percentage
import com.grocery.app.models.Product

class DiscountPageVH(private val binder : DiscountItemBinding): BaseVH<DiscountItemBinding, Product>(binder) {
    override fun bind(data: Product) {
        val context = itemView.context
        binder.tvProductDescription.text = data.name
        binder.tvDiscountPer.text = data.discount?.toInt().toString()
        val discount = data.discount
        val actualPrice = data.price
        val discountedPrice = actualPrice?.minus(actualPrice.percentage(discount?:0.0))
        binder.tvDiscountedPrice.text = context.getString(R.string.rs_symbol, discountedPrice.toString())
        binder.itemImage.loadImage(data.url)
    }
}