package com.grocery.app.homePage.dataModel

import com.grocery.app.constant.HomeCarousel
import com.grocery.app.models.Category


data class ItemGroup(
    var headerTitle: String? = null,
    var seeAllBtn: String? = null,
    var listItem: ArrayList<Category>? = null,
    var carousel: HomeCarousel
)