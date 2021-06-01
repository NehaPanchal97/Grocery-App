package com.grocery.app.viewHolders

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.grocery.app.R
import com.grocery.app.databinding.OrderStatusItemBinding
import com.grocery.app.extensions.formatDate
import com.grocery.app.extensions.visible
import com.grocery.app.models.OrderStatus
import com.grocery.app.models.Product

class OrderStatusVH(private val binder: OrderStatusItemBinding) :
        BaseVH<OrderStatusItemBinding, OrderStatus>(binder) {


    override fun bind(data: OrderStatus) {

        val statusBg = when {
            data.status == com.grocery.app.constant.OrderStatus.DECLINED.title -> R.drawable.close_google
            data.completed == true -> R.drawable.circle_check
            else -> R.drawable.cart_uncheck_circle
        }

        if (data.status == com.grocery.app.constant.OrderStatus.DELIVERED.title || data.status == com.grocery.app.constant.OrderStatus.DECLINED.title)
            binder.connectingLine.visible(false)
        else binder.connectingLine.visible(true)

        binder.statusText.text = data.title
        binder.description.text = data.description
        binder.time.text = data.updatedAt?.toDate()?.formatDate("hh:mm a dd-MM-yyyy")
        binder.check.background = ContextCompat.getDrawable(itemView.context, statusBg)
        binder.executePendingBindings()
    }

}