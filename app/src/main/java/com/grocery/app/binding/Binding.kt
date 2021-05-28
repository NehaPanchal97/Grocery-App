package com.grocery.app.binding

import android.graphics.Paint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.grocery.app.R
import com.grocery.app.constant.OrderStatus
import com.grocery.app.extensions.*
import com.grocery.app.models.Order
import com.grocery.app.models.Product


@BindingAdapter("loadUrl", "circular", "placeholder")
fun ImageView.loadUrl(
    url: String?,
    circular: Boolean = false,
    placeholder: Int = R.drawable.ic_shop
) {
    this.loadImage(url, circular = circular, placeholder = placeholder)
}

@BindingAdapter("orderStatus")
fun TextView.setOrderStatus(order: Order?) {
    val status = order?.currentStatus ?: ""
    val bg: Int
    val textColor: Int
    when (OrderStatus.fromString(status)) {
        OrderStatus.PLACED -> {
            bg = R.drawable.order_status_placed_bg
            textColor = R.color.placed_state_color
        }
        OrderStatus.DELIVERED -> {
            bg = R.drawable.order_status_bg
            textColor = R.color.success_color
        }
        OrderStatus.CANCELLED,
        OrderStatus.DECLINED -> {
            bg = R.drawable.order_status_cancel_bg
            textColor = R.color.error_color
        }
        else -> {
            //For Dispatched,Packed & Other cases
            bg = R.drawable.order_status_pending_bg
            textColor = R.color.pending_color
        }
    }
    this.text = order?.currentStatus?.capitalize()
    this.setTextColor(ContextCompat.getColor(this.context, textColor))
    this.background = ContextCompat.getDrawable(this.context, bg)
}

@BindingAdapter("orderCreatedDate")
fun TextView.setOrderCreatedDate(order: Order?) {
    val createdAt = order?.createdAt?.toDate()?.formatDate("dd MMM, YYYY hh:mm a")
    this.text = this.context.getString(R.string.order_created_at, createdAt)
    this.visible(createdAt?.isNotEmpty() == true)
}

@BindingAdapter("orderUrl")
fun ImageView.setOrderUrl(order: Order?) {
    this.loadUrl(order?.items?.firstOrNull()?.url)
}

@BindingAdapter("itemsInOrder")
fun TextView.setItemsInOrder(order: Order?) {
    val itemCount = order?.items?.size ?: 0
    this.text = "$itemCount Items"
}

@BindingAdapter("productPrice")
fun TextView.setProductPrice(product: Product?) {
    this.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    this.text = product?.price?.trim
    val discount = product?.discount ?: 0.0
    this.visible(discount > 0)
}

@BindingAdapter("productDiscountedPrice")
fun TextView.setProductDiscountedPrice(product: Product?) {

    val discount = product?.discount ?: 0.0
    val actualPrice = product?.price ?: 0.0
    val discountedPrice = actualPrice - actualPrice.percentage(discount)
    val priceToShow = if (discount > 0) discountedPrice else actualPrice
    this.text = this.context.getString(R.string.rs, priceToShow.trim)
}

@BindingAdapter("productDiscount")
fun TextView.setProductDiscount(product: Product?) {
    val discount = product?.discount ?: 0.0
    this.text = "${discount.trim}% off"
    this.visible(discount > 0)
}
