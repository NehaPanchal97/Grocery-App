package com.grocery.app.extensions

import java.lang.Exception


fun String?.toInt(default: Int = 0): Int {
    return try {
        Integer.parseInt(this ?: "")
    } catch (e: Exception) {
        default
    }
}

fun String?.toDouble(default: Double = 0.0): Double {
    return try {
        java.lang.Double.parseDouble(this ?: "")
    } catch (e: Exception) {
        default
    }
}
