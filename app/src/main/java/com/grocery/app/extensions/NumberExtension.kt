package com.grocery.app.extensions

import java.lang.Exception


fun String?.toInt(default: Int = 0): Int {
    return try {
        return Integer.parseInt(this ?: "")
    } catch (e: Exception) {
        default
    }
}