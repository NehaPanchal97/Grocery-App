package com.grocery.app.models

class Category(
    var name: String? = null,
    var id: String? = null,
    var rank: Int? = null,
    var url: String? = null
) : Cloneable {

    override fun clone(): Any {
        return Category(
            name, id, rank, url
        )
    }
}