package com.grocery.app.customs.pages

import android.content.Context
import androidx.fragment.app.Fragment
import com.grocery.app.R
import com.grocery.app.customs.Page
import com.grocery.app.fragments.AdminOrderListFragment
import com.grocery.app.fragments.CategoryListFragment

class AdminOrderPage : Page() {
    override fun getToolbar(context: Context): String {
        return context.getString(R.string.order)
    }

    override fun getFragment(): Fragment {
        return AdminOrderListFragment()
    }
}