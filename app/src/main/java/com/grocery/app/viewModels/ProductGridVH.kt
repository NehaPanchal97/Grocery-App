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


        val context = itemView.context
        val price = data.price
        val discount = data.discount
        val count = cartMap[data.id]?.count ?: 0
        binder.specificItemTitle.text = data.name
        binder.tvDiscount.text = context.getString(R.string.per_symbol, discount?.toInt().toString())
        binder.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        val discountedPrice =price?.minus(price.percentage(discount?:0.0))
        binder.tvDiscountedPrice.text = context.getString(R.string.rs_symbol, discountedPrice?.toInt().toString())
        binder.tvPrice.text =context.getString(R.string.rs_symbol, price?.toInt().toString())
        binder.itemImage.loadImage(url = data.url)
        if (count>0){
            binder.tvCount.text = "$count"
            binder.ivRemove.visible(true)
            binder.tvCount.visible(true)
        }else{
            binder.ivRemove.visible(false)
            binder.tvCount.visible(false)
        }

        if (discount==0.0){
            binder.tvDiscount.visible(false)
            binder.tvPrice.visible(false)
        }

    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}