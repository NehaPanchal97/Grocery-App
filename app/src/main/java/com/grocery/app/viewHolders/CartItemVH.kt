package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.databinding.CartItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.visible
import com.grocery.app.models.Product
import com.grocery.app.viewModels.ProductViewModel

class CartItemVH(private val binder: CartItemBinding,private val cartMap:HashMap<String,Product?>):
        BaseVH<CartItemBinding, Product>(binder){

    private val clickListener = View.OnClickListener { v -> itemClickListener?.onItemClick(v?.id?:-1,bindingAdapterPosition) }
     init {
         binder.ivCartAdd.setOnClickListener(clickListener)
         binder.ivCartRemove.setOnClickListener(clickListener)
     }

    override fun bind(data: Product) {

        val count = cartMap[data.id]?.count ?: 0
        binder.cartItemTitle.text = data.name
        binder.cartItemPrice.text = data.price.toString()
        binder.cartItemImage.loadImage(url = data.url)
        binder.tvCartCount.text = "$count"
    }


 }