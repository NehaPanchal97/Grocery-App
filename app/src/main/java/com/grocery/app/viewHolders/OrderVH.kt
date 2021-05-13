package com.grocery.app.viewHolders

import android.view.View
import androidx.core.content.ContextCompat
import com.grocery.app.R
import com.grocery.app.constant.OrderStatus
import com.grocery.app.databinding.AdminOrderListItemBinding
import com.grocery.app.extensions.formatDate
import com.grocery.app.models.Order
import java.util.*

class OrderVH(private val binder: AdminOrderListItemBinding) :
    BaseVH<AdminOrderListItemBinding, Order>(binder), View.OnClickListener {

    init {
        binder.root.setOnClickListener(this)
    }

    override fun bind(data: Order) {

        val res = itemView.context.resources
        val itemCount = data.items?.size ?: 0
        val statusBg = if (data.currentStatus == OrderStatus.CANCELLED.title)
            R.drawable.order_status_cancel_bg
        else R.drawable.order_status_bg
        val statusColor = if (data.currentStatus == OrderStatus.CANCELLED.title)
            R.color.error_color
        else R.color.success_color
        val createdAt = data.createdAt?.toDate()?.formatDate("dd MMM, YYYY")
        binder.name = data.name
        binder.address = data.deliveryAddress
        binder.placeholder = R.drawable.category_placeholder
        binder.itemCount = "$itemCount Items"
        binder.url = data.items?.firstOrNull()?.url
        binder.status = data.currentStatus?.capitalize()
        binder.orderCreated = res.getString(R.string.order_created_at, createdAt)
        binder.statusTv.background = ContextCompat.getDrawable(itemView.context, statusBg)
        binder.statusColor = ContextCompat.getColor(itemView.context, statusColor)
        binder.executePendingBindings()
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}