package com.grocery.app.listeners

import com.grocery.app.models.Category

interface OnCategoryClickListener {
    fun onItemClick(itemId: Int, category: Category)
}
