package com.grocery.app.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.grocery.app.R
import com.grocery.app.constant.*
import com.grocery.app.extensions.authUser
import com.grocery.app.extensions.logD
import com.grocery.app.listeners.SendNotificationListener
import com.grocery.app.models.NotificationPayload
import com.grocery.app.models.Order
import com.grocery.app.models.User
import com.grocery.app.utils.NotificationUtils

class OrderChangeService : Service() {


    companion object {

        const val TAG = "OrderChangeService"
        fun getIntent(context: Context, order: Order?): Intent {
            return Intent(context, OrderChangeService::class.java).apply {
                putExtra(ORDER, order)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val order = intent?.getParcelableExtra<Order>(ORDER)
        return when (OrderStatus.fromString(order?.currentStatus)) {
            OrderStatus.PLACED -> {
                sendOrderPlacedNotification(order)
                START_NOT_STICKY
            }
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING,
            OrderStatus.DELIVERED,
            OrderStatus.DECLINED -> {
                sendOrderChangeNotification(order)
                START_NOT_STICKY
            }
            OrderStatus.CANCELLED -> {
                sendOrderCancelNotification(order)
                START_NOT_STICKY
            }
            else -> {
                stopSelf()
                START_NOT_STICKY
            }
        }
    }

    private fun sendOrderCancelNotification(order: Order?) {
        Firebase.firestore.collection(Store.USERS)
            .whereEqualTo(Store.ROLE, Store.ADMIN_ROLE)
            .get()
            .addOnSuccessListener { snapShot ->
                if (!snapShot.isEmpty) {
                    val tokens = snapShot.documents.map { it.getString(Store.FCM_TOKEN) }
                        .filter { it?.isNotEmpty() == true }
                    if (tokens.isNotEmpty()) {
                        val data = hashMapOf<String, String?>(
                            TITLE to getString(R.string.order_cancelled_title),
                            BODY to getString(R.string._order_cancelled, order?.name, order?.id)
                        )
                        val payload = NotificationPayload(
                            data = data,
                            registrationIds = ArrayList(tokens)
                        )
                        sendNotification(payload)
                        return@addOnSuccessListener
                    }
                }
                stopSelf()
            }
            .addOnFailureListener {
                stopSelf()
            }
    }

    private fun sendOrderChangeNotification(order: Order?) {
        Firebase.firestore.document(Store.USERS + "/" + order?.createdBy)
            .get()
            .addOnSuccessListener { snapShot ->
                if (snapShot.exists()) {
                    val fcmToken = snapShot.getString(Store.FCM_TOKEN)
                    val orderStatus = order?.allStatus
                        ?.firstOrNull { it.status == order.currentStatus }

                    if (fcmToken?.isNotEmpty() == true && orderStatus != null) {
                        val data = hashMapOf(
                            TITLE to orderStatus.title,
                            BODY to orderStatus.description
                        )
                        val payload = NotificationPayload(
                            registrationIds = arrayListOf(fcmToken),
                            data = data
                        )
                        sendNotification(payload)
                        return@addOnSuccessListener
                    }
                }
                stopSelf()
            }
            .addOnFailureListener {
                stopSelf()
            }
    }

    private fun sendOrderPlacedNotification(order: Order?) {
        Firebase.firestore.collection(Store.USERS)
            .whereEqualTo(Store.ROLE, Store.ADMIN_ROLE)
            .get()
            .addOnSuccessListener { adminSnap ->
                if (!adminSnap.isEmpty) {
                    val admins =
                        adminSnap.toObjects<User>()
                            .filter { it.fcmToken?.isNotEmpty() == true && it.id != authUser?.uid }
                    NotificationUtils.sendLocalNotification(
                        this,
                        title = getString(R.string.order_placed_text),
                        body = getString(R.string.order_placed_msg, order?.id)
                    )
                    sendOrderPlacedToAdmin(admins, order)
                } else {
                    stopSelf()
                }
            }
            .addOnFailureListener {
                stopSelf()
            }

    }

    private fun sendOrderPlacedToAdmin(admins: List<User>, order: Order?) {
        val fcmTokens = admins.map { it.fcmToken }
        if (fcmTokens.isEmpty()) {
            stopSelf()
            return
        }
        val data = hashMapOf<String, String?>(
            TITLE to getString(R.string.order_received),
            BODY to getString(R.string._has_placed_order, order?.name)
        )
        val payload = NotificationPayload(
            registrationIds = ArrayList(fcmTokens),
            data = data
        )
        sendNotification(payload)
    }

    private fun sendNotification(payload: NotificationPayload) {
        NotificationUtils.sendMessageOnFcm(payload, object : SendNotificationListener {
            override fun onSent() {
                TAG.logD("Notification Sent Successfully")
                stopSelf()
            }

            override fun onError() {
                TAG.logD("Error Sending Notification")
                stopSelf()
            }

        })
    }
}