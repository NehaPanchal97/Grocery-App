package com.grocery.app.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.CART
import com.grocery.app.constant.ORDERS
import com.grocery.app.constant.Store
import com.grocery.app.exceptions.OrderStatusChangeException
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.clone
import com.grocery.app.extensions.currentDate
import com.grocery.app.utils.OrderUtils

private typealias Result<T> = com.grocery.app.extras.Result<T>
private typealias Order = com.grocery.app.models.Order

class OrderViewModel : ViewModel() {

    private val _orderListLiveData by lazy { MutableLiveData<Result<ArrayList<Order>>>() }
    private val _updateOrderLiveData by lazy { MutableLiveData<Result<Void>>() }

    val orderListLiveData
        get() = _orderListLiveData

    val updateOrderLiveData
        get() = _updateOrderLiveData

    var orderUpdated = false

    lateinit var order: Order
    var orderCreatedBy: String? = null


    fun fetchOrders() {
        _orderListLiveData.value = Result.loading()
        var query = Firebase.firestore.collection(ORDERS)
            .orderBy(Store.CREATED_AT, Query.Direction.DESCENDING)
        orderCreatedBy?.let {
            query = query.whereEqualTo(Store.CREATED_BY, it)
        }
        query.get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val orders = it.result?.toObjects<Order>() ?: arrayListOf()
                    _orderListLiveData.value = Result.success(ArrayList(orders))
                } else {
                    _orderListLiveData.value = Result.error()
                }
            }
    }

    fun createOrder(order: Order, cartId: String?) {
        _updateOrderLiveData.value = Result.loading()
        val cartPath = "${Store.USERS}/${authUser?.uid}/$CART/$cartId"
        val cartRef = Firebase.firestore.document(cartPath)
        Firebase.firestore.collection(ORDERS)
            .document(order.id ?: "")
            .set(order, SetOptions.merge())
            .onSuccessTask {
                cartRef.delete()
            }
            .addOnSuccessListener {
                _updateOrderLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _updateOrderLiveData.value = Result.error()
            }
    }

    fun updateOrder() {
        _updateOrderLiveData.value = Result.loading()
        val db = Firebase.firestore
        val orderRef = db.document(ORDERS + "/" + order.id)
        db.runTransaction { transaction ->
            val orderSnap = transaction.get(orderRef)
            var newOrder = orderSnap.toObject<Order>()
            if (newOrder?.currentStatus != order.currentStatus && newOrder != null) {
                order = newOrder
                throw OrderStatusChangeException("Order status is changed")
            }
            newOrder = getUpdatedOrder()
            transaction.set(orderRef, newOrder, SetOptions.merge())
            newOrder
        }
            .addOnSuccessListener {
                order = it
                _updateOrderLiveData.value = Result.success()
            }
            .addOnFailureListener {
                _updateOrderLiveData.value = Result.error(it)
            }
    }

    private fun getUpdatedOrder(): Order {
        val newOrder = order.clone() ?: Order()
        val time = Timestamp(currentDate)
        val unCompleted = newOrder.allStatus?.firstOrNull { it.completed != true }
        unCompleted?.completed = true
        unCompleted?.updatedAt = time

        newOrder.currentStatus = unCompleted?.status
        newOrder.updatedAt = time
        return newOrder
    }
}