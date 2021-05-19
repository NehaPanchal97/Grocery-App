package com.grocery.app.customs

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.grocery.app.R

abstract class Page {
    abstract fun getToolbar(context: Context): String
    abstract fun getFragment(): Fragment

    open fun setupToolbar(toolbar: MaterialToolbar) {
        val menu = toolbar.menu
        menu.findItem(R.id.cart).isVisible = false
        menu.findItem(R.id.add).isVisible = false
        menu.findItem(R.id.filter).isVisible = false
    }
}