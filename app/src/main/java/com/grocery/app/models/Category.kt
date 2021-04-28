package com.grocery.app.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Category(
    var name: String? = null,
    var id: String? = null,
    var rank: Int? = null,
    var url: String? = null
) : Cloneable, Parcelable {

    override fun clone(): Any {
        return Category(
            name, id, rank, url
        )
    }
}