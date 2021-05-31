package com.grocery.app.viewModels

import android.graphics.Paint
import android.view.View
import com.grocery.app.R
import com.grocery.app.databinding.ProductItemWithPriceBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.percentage
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
        if(absoluteAdapterPosition%2==0){
            binder.verticalLine.visible(true)
        }else{
            binder.verticalLine.visible(false)
        }
        binder.product = data
        val count = cartMap[data.id]?.count ?: 0
        binder.specificItemTitle.text = data.name
        binder.itemImage.loadImage(url = data.url)
        if (count>0){
            binder.tvCount.text = "$count"
            binder.ivRemove.visible(true)
            binder.tvCount.visible(true)
        }else{
            binder.ivRemove.visible(false)
            binder.tvCount.visible(false)
        }
        binder.executePendingBindings()
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}