package com.grocery.app.extensions

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.grocery.app.BuildConfig
import java.text.DecimalFormat
import kotlin.math.pow

val authUser: FirebaseUser?
    get() = Firebase.auth.currentUser

inline fun <reified T> Any?.cast() = this as? T

fun Double.percentage(percentage: Double): Double {
    return (this * percentage).toInt() / 100.0
}

fun Double.round(place: Int): Double {
    val multiplier = 10.0.pow(place)
    return this.times(multiplier)
        .toInt().toDouble()
        .div(multiplier)
}

val Double.trim: String
    get() {
        val formatter = DecimalFormat("#.#")
        return formatter.format(this)
    }

fun String.logD(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(this, message)
    }
}

val String.isRemoteUrl
    get() = this.startsWith("https://") || this.startsWith("http://")

val String.searchKeys: ArrayList<String>
    get() {
        val keys = arrayListOf<String>()
        val words = this.trim()split(" ")
        words.forEach { word ->
            if (word.length > 2) {
                for (i in 3..word.length) {
                    keys.add(word.substring(0, i).toLowerCase())
                }
            }
        }
        if (words.size > 1) {
            for (i in 1 until words.size) {
                val wordGroup = arrayListOf<String>()
                for (j in 0..i) {
                    wordGroup.add(words[j])
                }
                keys.add(wordGroup.joinToString(" ").toLowerCase())
            }
        }
        return keys
    }