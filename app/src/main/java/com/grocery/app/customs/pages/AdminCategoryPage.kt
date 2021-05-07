package com.grocery.app.customs.pages

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.grocery.app.R
import com.grocery.app.fragments.CategoryListFragment
import com.grocery.app.customs.Page

class AdminCategoryPage : Page() {
    override fun getToolbar(context: Context): String {
        return context.getString(R.string.category)
    }

    override fun getFragment(): Fragment {
        return CategoryListFragment()
    }

    override fun setupToolbar(toolbar: MaterialToolbar) {
        super.setupToolbar(toolbar)
        toolbar.menu.findItem(R.id.add).isVisible = true
    }
}