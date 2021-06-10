package com.grocery.app.viewHolders

import android.graphics.Paint
import android.view.View
import com.grocery.app.R
import com.grocery.app.databinding.DiscountItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.percentage
import com.grocery.app.models.Product

class DiscountPageVH(private val binder : DiscountItemBinding): BaseVH<DiscountItemBinding, Product>(binder) ,
    View.OnClickListener {
    override fun bind(data: Product) {
        val context = itemView.context
        binder.tvProductDescription.text = data.name?.trim()
       val discountPercent =  data.discount?.toInt().toString()
        binder.tvDiscountPer.text = context.getString(R.string.discount_percent,discountPercent)
        val discount = data.discount
        val actualPrice = data.price
        binder.tvActualPrice.text = context.getString(R.string.rs_symbol,actualPrice?.toInt().toString())
        binder.tvActualPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        val discountedPrice = actualPrice?.minus(actualPrice.percentage(discount?:0.0))
        binder.tvDiscountedPrice.text = context.getString(R.string.rs_symbol, discountedPrice?.toInt().toString())
        binder.itemImage.loadImage(data.url)
    }

    init {
        binder.itemImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id?: -1, bindingAdapterPosition)
    }
}