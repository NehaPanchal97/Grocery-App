package com.grocery.app.viewHolders

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.grocery.app.adapters.ProductListAdapter
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.showSuccess
import com.grocery.app.homePage.OrderStatusPageActivity
import com.grocery.app.homePage.adapters.CategoryTypesAdapter
import com.grocery.app.listeners.OnCategoryClickListener
import com.grocery.app.listeners.OnItemClickListener
import com.grocery.app.models.Order
import com.grocery.app.models.Product
import com.grocery.app.viewModels.OrderViewModel

class OrderDescriptionItemVH (private val binder: OrderDescriptionItemBinding):
    BaseVH<OrderDescriptionItemBinding, Product>(binder){



    @SuppressLint("SetTextI18n")
    override fun bind(data: Product) {


        val price = data.price
        binder.orderDesItemTitle.text = data.name
        binder.orderDesItemPrice.text = " $ $price "
        binder.orderDesItemImage.loadImage(url = data.url)

    }


}