package com.grocery.app.customs

import com.grocery.app.R
import com.grocery.app.customs.pages.AdminCategoryPage
import com.grocery.app.customs.pages.AdminOrderPage
import com.grocery.app.customs.pages.AdminProductPage
import com.grocery.app.customs.pages.AdminSettingPage

class PageFactory {

    companion object {
        fun create(menuId: Int): Page {
            return when (menuId) {
                R.id.order -> AdminOrderPage()
                R.id.category -> AdminCategoryPage()
                R.id.product -> AdminProductPage()
                R.id.setting -> AdminSettingPage()
                else -> AdminCategoryPage()
            }
        }
    }
}