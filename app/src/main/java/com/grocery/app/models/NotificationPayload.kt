package com.grocery.app.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotificationPayload(
    @SerializedName("registration_ids")
    var registrationIds: ArrayList<String?>? = null,
    var data: HashMap<String, String?>? = null
)