package com.grocery.app.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Cart(
    var id: String? = null,
    var total: Double? = null,
    var items: ArrayList<Product>? = null
) : Parcelable