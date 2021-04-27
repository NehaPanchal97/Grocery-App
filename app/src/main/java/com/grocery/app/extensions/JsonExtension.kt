package com.grocery.app.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

inline fun <reified T> DocumentSnapshot?.toObj(): T? {
    return try {
        val data = this?.data ?: mapOf()
        val gSon = Gson()
        val json = gSon.toJson(data)
        gSon.fromJson(json, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified T> T.clone(): T? {
    return try {
        val gSon = Gson()
        val json = gSon.toJson(this)
        gSon.fromJson(json, T::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}