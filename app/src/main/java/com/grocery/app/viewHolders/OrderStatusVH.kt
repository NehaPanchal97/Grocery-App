package com.grocery.app.viewHolders

import androidx.core.content.ContextCompat
import com.grocery.app.R
import com.grocery.app.databinding.OrderStatusItemBinding
import com.grocery.app.extensions.visible
import com.grocery.app.models.OrderStatus

class OrderStatusVH (private val binder: OrderStatusItemBinding):
        BaseVH<OrderStatusItemBinding, OrderStatus>(binder){

     override fun bind(data:OrderStatus) {

         val statusBg = if (data.completed==true)
             R.drawable.circle_check
         else R.drawable.cart_uncheck_circle


         binder.statusText.text = data.status

         binder.description.text = data.description
         binder.userResponseTime = data.createdAt?.toDate().toString()
         binder.check.background = ContextCompat.getDrawable(itemView.context, statusBg)
         binder.executePendingBindings()
     }

}