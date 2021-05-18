package com.grocery.app.viewModels

import android.view.View
import com.grocery.app.databinding.ProductItemWithPriceBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.visible
import com.grocery.app.models.Product
import com.grocery.app.viewHolders.BaseVH

class ProductGridVH(private val binder: ProductItemWithPriceBinding) :
    BaseVH<ProductItemWithPriceBinding, Product>(binder), View.OnClickListener {

    lateinit var cartMap: HashMap<String, Product?>

    init {
        binder.ivAdd.setOnClickListener(this)
        binder.ivRemove.setOnClickListener(this)
        binder.itemImage.setOnClickListener(this)
    }

    override fun bind(data: Product) {


        val count = cartMap[data.id]?.count ?: 0
        binder.specificItemTitle.text = data.name
        binder.tvPrice.text = data.price.toString()
        binder.itemImage.loadImage(url = data.url)
        if (count>0){
            binder.tvCount.text = "$count"
            binder.ivRemove.visible(true)
            binder.tvCount.visible(true)
        }else{
            binder.ivRemove.visible(false)
            binder.tvCount.visible(false)
        }

    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}