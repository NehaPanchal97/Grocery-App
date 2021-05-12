package com.grocery.app.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.ORDERS

private typealias Result<T> = com.grocery.app.extras.Result<T>
private typealias Order = com.grocery.app.models.Order

class OrderViewModel : ViewModel() {

    private val _orderListLiveData by lazy { MutableLiveData<Result<ArrayList<Order>>>() }

    val orderListLiveData
        get() = _orderListLiveData


    fun fetchOrders() {
        _orderListLiveData.value = Result.loading()
        Firebase.firestore.collection(ORDERS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val orders = it.result?.toObjects<Order>() ?: arrayListOf()
                    _orderListLiveData.value = Result.success(ArrayList(orders))
                } else {
                    _orderListLiveData.value = Result.error()
                }
            }
    }
}