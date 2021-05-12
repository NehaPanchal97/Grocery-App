package com.grocery.app.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Order(
    val id: String? = null,
    val total: Double? = null,
    val updatedAt: Timestamp? = null,
    val currentStatus: String? = null,
    val createdBy: String? = null,
    val createdAt: Timestamp? = null,
    val allStatus: ArrayList<OrderStatus>? = null,
    val items: ArrayList<Product>? = null,
    val name: String? = null,
    val contact: String? = null,
    var deliveryAddress: String? = null
) : Parcelable

@Keep
@Parcelize
data class OrderStatus(
    val completed: Boolean? = null,
    val createdAt: Timestamp? = null,
    val description: String? = null,
    val status: String? = null,
    val updatedAt: Timestamp? = null
) : Parcelable