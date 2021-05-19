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
        binder.order = data
        binder.executePendingBindings()
    }

    override fun onClick(v: View?) {
        itemClickListener?.onItemClick(v?.id ?: -1, bindingAdapterPosition)
    }
}