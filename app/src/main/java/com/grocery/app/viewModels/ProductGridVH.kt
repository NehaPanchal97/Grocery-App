package com.grocery.app.viewModels

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.databinding.ProductItemWithPriceBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH

class ProductGridVH(private val binder: ProductItemWithPriceBinding) :
    BaseVH<ProductItemWithPriceBinding, Product>(binder) {

    override fun bind(data: Product) {

        binder.specificItemTitle.text = data.name
        binder.tvPrice.text = data.price.toString()
        binder.itemImage.loadImage(url = data.url)
    }
}