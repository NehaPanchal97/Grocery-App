package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.R
import com.grocery.app.databinding.ProductListItemBinding
import com.grocery.app.models.Product

class AdminProductItemVH(private val binder: ProductListItemBinding) :
    BaseVH<ProductListItemBinding, Product>(binder), View.OnClickListener {


    override fun bind(data: Product) {
        binder.product = data
        binder.editIv.setOnClickListener(this)
        binder.executePendingBindings()

    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }

}