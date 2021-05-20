package com.grocery.app.viewHolders

import android.annotation.SuppressLint
import android.view.View

import com.grocery.app.R
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

class OrderDescriptionItemVH (private val binder: OrderDescriptionItemBinding):
    BaseVH<OrderDescriptionItemBinding, Product>(binder),View.OnClickListener{



    @SuppressLint("SetTextI18n")
    override fun bind(data: Product) {
        val context = itemView.context

        val price = data.price
        binder.orderDesItemTitle.text = data.name
        binder.orderDesItemPrice.text =  context.getString(R.string.rs_symbol, price.toString())
        binder.orderDesItemImage.loadImage(url = data.url)
    }

    init {
        binder.orderDesItemImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id?:-1,bindingAdapterPosition)
    }
}


