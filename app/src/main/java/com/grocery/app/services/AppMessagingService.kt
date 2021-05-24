package com.grocery.app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.grocery.app.MainActivity
import com.grocery.app.R
import com.grocery.app.activities.SplashActivity
import com.grocery.app.constant.*
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.logD
import com.grocery.app.extensions.toJson
import com.grocery.app.utils.NotificationUtils
import com.grocery.app.utils.PrefManager
import kotlin.random.Random


class AppMessagingService : FirebaseMessagingService() {

    private val pref by lazy { PrefManager.getInstance(this) }

    companion object {
        const val TAG = "AppMessagingService"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        TAG.logD("New Notification Received")
        if (message.data.isNotEmpty()) {
            val data = message.data
            TAG.logD("Payload: " + data.toJson())
            NotificationUtils.sendLocalNotification(
                this,
                title = data[TITLE],
                body = data[BODY]
            )
        }

    }

    override fun onNewToken(fcmToken: String) {
        TAG.logD("Refreshed Token: $fcmToken")
        pref.putString(FCM_TOKEN, fcmToken)
        if (authUser != null && pref.contains(USER)) {
            sendTokenToFirestore(fcmToken)
        }
    }


    private fun sendTokenToFirestore(fcmToken: String) {
        Firebase.firestore
            .document(Store.USERS + "/" + authUser?.uid)
            .update(Store.FCM_TOKEN, fcmToken)
            .addOnSuccessListener {
                TAG.logD("fcm token updated successfully.")
            }
            .addOnFailureListener {
                TAG.logD("Failed updating fcm token")
            }
    }
}