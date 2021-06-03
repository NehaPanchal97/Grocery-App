package com.grocery.app.models

import androidx.annotation.Keep

@Keep
data class AboutPage(
    var locationUri: String? = null,
    var storeName:String? = null,
    var contact: String? = null,
    var timing:String? = null,
    var callDescription:String? = null,
    var locationDescription:String? = null
)