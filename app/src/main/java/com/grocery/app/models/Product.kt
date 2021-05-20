package com.grocery.app.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.grocery.app.constant.Store
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Product(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var price: Double? = null,
    var active: Boolean? = null,
    @SerializedName(Store.CATEGORY_ID)
    var categoryId: String? = null,
    var url: String? = null,
    var tags: ArrayList<String?>? = null,
    var count: Int? = null,
    var total: Double? = null,
    var discount: Int? = null

) : Parcelable