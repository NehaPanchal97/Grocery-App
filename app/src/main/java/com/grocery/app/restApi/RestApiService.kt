package com.grocery.app.restApi

import com.google.gson.JsonObject
import com.grocery.app.constant.FCM_SERVER_KEY
import com.grocery.app.models.NotificationPayload
import com.grocery.app.models.Order
import com.squareup.okhttp.Response
import retrofit2.Call
import retrofit2.http.*

interface RestApiService {

    @Headers("Authorization: key=$FCM_SERVER_KEY")
    @POST("/fcm/send")
    fun sendNotification(@Body body: NotificationPayload): Call<JsonObject>
}