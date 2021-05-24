package com.grocery.app.utils

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.R
import com.grocery.app.constant.ORDERS
import com.grocery.app.models.Order
import com.grocery.app.models.OrderStatus
import com.grocery.app.models.Product
import java.util.*
import kotlin.collections.ArrayList

private typealias Status = com.grocery.app.constant.OrderStatus

object OrderUtils {

    fun createOrder(
        context: Context,
        products: ArrayList<Product>,
        total: Double,
        userId: String,
        name: String,
        contact: String,
        address: String,
        totalDiscount: Double,
        payableAmount: Double
    ): Order {

        val id = Firebase.firestore.collection(ORDERS).document().id
        val time = Timestamp(Date())
        val allStatus = createOrderStatus(context, time, id)
        return Order(
            id = id,
            allStatus = allStatus,
            createdAt = time,
            createdBy = userId,
            currentStatus = Status.PLACED.title,
            items = products,
            total = total,
            totalDiscount = totalDiscount,
            payableAmount = payableAmount,
            updatedAt = time,
            contact = contact,
            deliveryAddress = address,
            name = name
        )
    }

    private fun createOrderStatus(
        context: Context,
        time: Timestamp,
        orderId: String
    ): ArrayList<OrderStatus> {
        val status = arrayListOf<OrderStatus>()
        status.add(
            OrderStatus(
                true, time,
                context.getString(R.string.order_placed_msg, orderId),
                Status.PLACED.title,
                updatedAt = time,
                title = context.getString(R.string.order_placed_title)
            )
        )
        status.add(
            OrderStatus(
                false,
                time,
                context.getString(R.string.order_confirmed_msg), Status.CONFIRMED.title,
                title = context.getString(R.string.order_confirmed_title)
            )
        )
        status.add(
            OrderStatus(
                false, time,
                context.getString(R.string.order_processing_msg), Status.PROCESSING.title,
                title = context.getString(R.string.order_processing_title)
            )
        )
        status.add(
            OrderStatus(
                false, time,
                context.getString(R.string.order_delivered_msg), Status.DELIVERED.title,
                title = context.getString(R.string.order_delivered_title)
            )
        )
        return status
    }
}