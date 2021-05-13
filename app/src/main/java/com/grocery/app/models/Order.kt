package com.grocery.app.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Order(
    var id: String? = null,
    var total: Double? = null,
    var updatedAt: Timestamp? = null,
    var currentStatus: String? = null,
    var createdBy: String? = null,
    var createdAt: Timestamp? = null,
    var allStatus: ArrayList<OrderStatus>? = null,
    var items: ArrayList<Product>? = null,
    var name: String? = null,
    var contact: String? = null,
    var deliveryAddress: String? = null
) : Parcelable

@Keep
@Parcelize
data class OrderStatus(
    var completed: Boolean? = null,
    var createdAt: Timestamp? = null,
    var description: String? = null,
    var status: String? = null,
    var updatedAt: Timestamp? = null
) : Parcelable