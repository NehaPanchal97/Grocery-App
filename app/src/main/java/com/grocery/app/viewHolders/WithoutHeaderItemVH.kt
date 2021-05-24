package com.grocery.app.viewHolders

import com.grocery.app.databinding.CardviewWithoutHeaderBinding
import com.grocery.app.databinding.OrderDescriptionItemBinding
import com.grocery.app.extensions.loadImage
import com.grocery.app.models.Category
import com.grocery.app.models.Product

class WithoutHeaderItemVH(private val binder: CardviewWithoutHeaderBinding) :
    BaseVH<CardviewWithoutHeaderBinding, Category>(binder) {
    override fun bind(data: Category) {
        binder.offerText.text = data.offerDescription
        binder.cardviewImage.loadImage(url = data.url)
        binder.discount.text = data.offerTitle
    }
}