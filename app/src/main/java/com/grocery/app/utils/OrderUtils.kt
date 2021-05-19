package com.grocery.app.utils

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.R
import com.grocery.app.constant.ORDERS
import com.grocery.app.extensions.authUser
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
        address: String
    ): Order {

        val id = Firebase.firestore.collection(ORDERS).document().id
        val time = Timestamp(Date())
        val allStatus = createOrderStatus(context, time)
        return Order(
            id = id,
            allStatus = allStatus,
            createdAt = time,
            createdBy = userId,
            currentStatus = Status.PLACED.title,
            items = products,
            total = total,
            updatedAt = time,
            contact = contact,
            deliveryAddress = address,
            name = name
        )
    }

    private fun createOrderStatus(context: Context, time: Timestamp): ArrayList<OrderStatus> {
        val status = arrayListOf<OrderStatus>()
        status.add(
            OrderStatus(
                true, time,
                context.getString(R.string.order_placed_msg), Status.PLACED.title,
                updatedAt = time
            )
        )
        status.add(
            OrderStatus(
                false, time,
                context.getString(R.string.order_packed_msg), Status.PACKED.title
            )
        )
        status.add(
            OrderStatus(
                false, time,
                context.getString(R.string.order_dispatched_msg), Status.DISPATCHED.title
            )
        )
        status.add(
            OrderStatus(
                false, time,
                context.getString(R.string.order_delivered_msg), Status.DELIVERED.title
            )
        )
        return status
    }
}