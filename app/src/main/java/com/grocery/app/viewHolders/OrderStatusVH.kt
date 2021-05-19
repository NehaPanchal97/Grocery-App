package com.grocery.app.viewHolders

import androidx.core.content.ContextCompat
import com.grocery.app.R
import com.grocery.app.databinding.OrderStatusItemBinding
import com.grocery.app.extensions.formatDate
import com.grocery.app.extensions.visible
import com.grocery.app.models.OrderStatus

class OrderStatusVH (private val binder: OrderStatusItemBinding):
        BaseVH<OrderStatusItemBinding, OrderStatus>(binder){


     override fun bind(data:OrderStatus) {

         val statusBg = if (data.completed==true)
             R.drawable.circle_check
         else R.drawable.cart_uncheck_circle


          if (data.status == com.grocery.app.constant.OrderStatus.DELIVERED.title)
             binder.connectingLine.visible(false)
         else binder.connectingLine.visible(true)

         binder.statusText.text = data.status?.capitalize()
         binder.description.text = data.description
         binder.userResponseTime = data.updatedAt?.toDate()?.formatDate("dd-MM-yyyy , h:m a")
         binder.check.background = ContextCompat.getDrawable(itemView.context, statusBg)

         binder.executePendingBindings()
     }

}