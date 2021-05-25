package com.grocery.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.gson.JsonObject
import com.grocery.app.R
import com.grocery.app.activities.SplashActivity
import com.grocery.app.listeners.SendNotificationListener
import com.grocery.app.models.NotificationPayload
import com.grocery.app.restApi.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

object NotificationUtils {

    fun sendMessageOnFcm(
        payload: NotificationPayload,
        listener: SendNotificationListener? = null
    ) {

        RestClient.getInstance().service()
            .sendNotification(payload)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        listener?.onSent()
                    } else {
                        listener?.onError()
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    listener?.onError()
                }

            })
    }

    fun sendLocalNotification(
        context: Context,
        notificationId: Int? = null,
        title: String?,
        body: String?
    ) {
        val intent = Intent(context, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = context.getString(com.grocery.app.R.string.grocery_notification_id)
        val bigTextStyle = NotificationCompat.BigTextStyle().bigText(body)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_shop)
            .setContentTitle(title)
            .setStyle(bigTextStyle)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.grocery_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(
            notificationId ?: Random.nextInt(),
            notificationBuilder.build()
        )
    }
}