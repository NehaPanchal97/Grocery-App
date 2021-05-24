package com.grocery.app.constant

enum class OrderStatus(val title: String) {
    PLACED("placed"),
    CONFIRMED("confirmed"),
    PROCESSING("processing"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    OTHER("other")
}
