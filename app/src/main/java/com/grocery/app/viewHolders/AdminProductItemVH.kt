package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.R
import com.grocery.app.databinding.ProductListItemBinding
import com.grocery.app.models.Product

class AdminProductItemVH(private val binder: ProductListItemBinding) :
    BaseVH<ProductListItemBinding, Product>(binder), View.OnClickListener {


    override fun bind(data: Product) {
        binder.name = data.name
        binder.url = data.url
        binder.description = data.description
        binder.placeholder = R.drawable.category_placeholder
        binder.price = "Price: ${data.price}"
        binder.editIv.setOnClickListener(this)
        binder.executePendingBindings()

    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }

}