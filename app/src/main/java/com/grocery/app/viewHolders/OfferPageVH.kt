package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.databinding.OfferPageItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Category

class OfferPageVH(private val binder: OfferPageItemBinding):
        BaseVH<OfferPageItemBinding, Category>(binder), View.OnClickListener{

    init {
        binder.itemDiscountContainer.setOnClickListener(this)
    }

    override fun bind(data: Category) {
        binder.offerDescription.text = data.offerDescription
        binder.itemImage.loadImage(url = data.url)
        binder.offerDiscountTitle.text = data.offerTitle
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id?:-1,bindingAdapterPosition)
    }

}