package com.grocery.app.constant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class OrderStatus(val title: String) : Parcelable {
    PLACED("placed"),
    CONFIRMED("confirmed"),
    PROCESSING("processing"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    OTHER("other");

    companion object {
        fun fromString(title: String?): OrderStatus {
            if (title == null) {
                return OTHER
            }
            return when (title) {
                PLACED.title -> PLACED
                CONFIRMED.title -> CONFIRMED
                PROCESSING.title -> PROCESSING
                DELIVERED.title -> DELIVERED
                CANCELLED.title -> CANCELLED
                else -> OTHER
            }
        }
    }

}
