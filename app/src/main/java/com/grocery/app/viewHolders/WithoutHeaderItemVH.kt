package com.grocery.app.viewHolders

import android.view.View
import com.grocery.app.databinding.CardviewWithoutHeaderBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Category

class WithoutHeaderItemVH(private val binder: CardviewWithoutHeaderBinding) :
    BaseVH<CardviewWithoutHeaderBinding, Category>(binder),View.OnClickListener{
    var data: Category?=null
    init {
        binder.discountContainer.setOnClickListener(this)
    }
    override fun bind(data: Category) {
        this.data = data
        binder.offerText.text = data.offerDescription
        binder.cardviewImage.loadImage(url = data.url)
        binder.discount.text = data.offerTitle
    }

    override fun onClick(v: View?) {
        data?.let { categoryClickListener?.onItemClick(v?.id?:-1, it )}
    }

}