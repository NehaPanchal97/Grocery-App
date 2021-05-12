package com.grocery.app.constant

enum class OrderStatus(val title: String) {
    PLACED("placed"),
    PACKED("packed"),
    DISPATCHED("dispatched"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    OTHER("other")
}
