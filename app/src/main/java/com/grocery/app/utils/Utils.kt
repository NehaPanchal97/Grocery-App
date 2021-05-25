package com.grocery.app.utils

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.grocery.app.constant.Store
import com.grocery.app.constant.Store.USERS
import com.grocery.app.extensions.authUser
import okhttp3.internal.wait


fun String?.isBlank(): Boolean {
    return this.isNullOrBlank()
}

fun Context.signOut() {
    Firebase.firestore.document(USERS + "/" + authUser?.uid)
        .update(Store.FCM_TOKEN, null).addOnCompleteListener { }
    Firebase.auth.signOut()
    PrefManager.getInstance(this).clear()
}