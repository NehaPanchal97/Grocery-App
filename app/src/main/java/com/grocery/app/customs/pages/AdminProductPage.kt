package com.grocery.app.customs.pages

import android.content.Context
import androidx.fragment.app.Fragment
import com.grocery.app.R
import com.grocery.app.customs.Page
import com.grocery.app.fragments.ProductListFragment

class AdminProductPage : Page() {
    override fun getToolbar(context: Context): String {
        return context.getString(R.string.product)
    }

    override fun getFragment(): Fragment {
        return ProductListFragment()
    }
}