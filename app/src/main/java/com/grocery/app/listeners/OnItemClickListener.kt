package com.grocery.app.listeners

interface OnItemClickListener {
    //itemId is the id of the view clicked
    //position is the position of VH
    fun onItemClick(itemId: Int, position: Int)
}