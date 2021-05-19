package com.grocery.app.viewHolders

import android.view.View
import androidx.core.content.ContextCompat
import com.grocery.app.R
import com.grocery.app.constant.OrderStatus
import com.grocery.app.databinding.OrderItemBinding
import com.grocery.app.extensions.formatDate
import com.grocery.app.models.Order

class OrderItemVH(
    private val binder: OrderItemBinding
) : BaseVH<OrderItemBinding, Order>(binder), View.OnClickListener {

    override fun bind(data: Order) {
        val context = itemView.context
        val itemCount = data.items?.size ?: 0
        val createdAt = data.createdAt?.toDate()?.formatDate("dd MMM, YYYY")
        val deliveredAt = data.allStatus?.firstOrNull {
            it.completed == true && it.status == OrderStatus.DELIVERED.title
        }?.updatedAt?.toDate()?.formatDate("dd MMM, YYYY")
        val statusColor = if (data.currentStatus == OrderStatus.CANCELLED.title)
            R.color.error_color
        else R.color.success_color
        if (data.currentStatus == OrderStatus.PLACED.title) {
            binder.orderCreatedAt = context.getString(R.string.orderCreated, createdAt)
        } else {
            binder.orderCreatedAt = context.getString(R.string.order_delivered_at, deliveredAt)
        }

        binder.itemCount = "$itemCount Items"
        binder.url = data.items?.firstOrNull()?.url
        binder.name = data.items?.firstOrNull()?.name
        binder.status = data.currentStatus?.capitalize()
        binder.statusColor = ContextCompat.getColor(itemView.context, statusColor)
        binder.executePendingBindings()
    }

    init {
        binder.containerOrderItem.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}