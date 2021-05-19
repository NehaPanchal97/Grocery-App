package com.grocery.app.viewHolders

import android.annotation.SuppressLint
import android.view.View
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product

class OrderDescriptionItemVH (private val binder: OrderDescriptionItemBinding):
    BaseVH<OrderDescriptionItemBinding, Product>(binder){



    @SuppressLint("SetTextI18n")
    override fun bind(data: Product) {


        val price = data.price
        binder.orderDesItemTitle.text = data.name
        binder.orderDesItemPrice.text = " $price"
        binder.orderDesItemImage.loadImage(url = data.url)
    }

    }


