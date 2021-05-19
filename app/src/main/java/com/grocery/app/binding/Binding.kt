package com.grocery.app.binding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.grocery.app.R
import com.grocery.app.constant.OrderStatus
import com.grocery.app.extensions.formatDate
import com.grocery.app.extensions.loadImage
import com.grocery.app.extensions.visible
import com.grocery.app.models.Order


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
    when (status) {
        OrderStatus.PLACED.title -> {
            bg = R.drawable.order_status_placed_bg
            textColor = R.color.placed_state_color
        }
        OrderStatus.DELIVERED.title -> {
            bg = R.drawable.order_status_bg
            textColor = R.color.success_color
        }
        OrderStatus.CANCELLED.title -> {
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
    val createdAt = order?.createdAt?.toDate()?.formatDate("dd MMM, YYYY")
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